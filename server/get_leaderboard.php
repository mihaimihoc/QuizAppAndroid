<?php
include 'config.php';

header("Content-Type: application/json");

try {
    $category = isset($_GET['category']) ? $_GET['category'] : 'All Categories';
    $userId = isset($_GET['user_id']) ? intval($_GET['user_id']) : null;
    
    // Main leaderboard query - updated to include color_code
    $query = "SELECT 
                u.id as user_id, 
                u.name, 
                SUM(up.calculated_score) as total_score,
                SUM(up.response_time_ms) as total_time,
                GROUP_CONCAT(DISTINCT q.category ORDER BY q.category SEPARATOR ', ') as category,
                COALESCE(pi.color_code, 'default') as color_code
              FROM accounts u
              JOIN user_progress up ON u.id = up.user_id
              JOIN questions q ON up.question_id = q.id
              LEFT JOIN user_inventory ui ON u.id = ui.user_id AND ui.is_selected = 1
              LEFT JOIN profile_items pi ON ui.item_id = pi.id";
    
    if ($category != 'All Categories') {
        $query .= " WHERE q.category = '" . $conn->real_escape_string($category) . "'";
    }
    
    $query .= " GROUP BY u.id
                ORDER BY total_score DESC, total_time ASC
                LIMIT 10";
    
    $result = $conn->query($query);
    $leaderboard = $result->fetch_all(MYSQLI_ASSOC);
    
    // User rank query - updated to include color_code
    $userRank = null;
    if ($userId !== null) {
        $rankQuery = "SELECT 
                        u.id,
                        u.name,
                        SUM(up.calculated_score) as total_score,
                        SUM(up.response_time_ms) as total_time,
                        COALESCE(pi.color_code, 'default') as color_code,
                        (
                            SELECT COUNT(*) + 1 
                            FROM (
                                SELECT 
                                    u2.id,
                                    SUM(up2.calculated_score) as score,
                                    SUM(up2.response_time_ms) as time
                                FROM accounts u2
                                JOIN user_progress up2 ON u2.id = up2.user_id
                                JOIN questions q2 ON up2.question_id = q2.id
                                " . ($category != 'All Categories' ? "WHERE q2.category = '" . $conn->real_escape_string($category) . "'" : "") . "
                                GROUP BY u2.id
                            ) as ranked_users
                            WHERE ranked_users.score > (
                                SELECT SUM(up.calculated_score)
                                FROM user_progress up
                                JOIN questions q ON up.question_id = q.id
                                WHERE up.user_id = ?
                                " . ($category != 'All Categories' ? "AND q.category = '" . $conn->real_escape_string($category) . "'" : "") . "
                            )
                            OR (
                                ranked_users.score = (
                                    SELECT SUM(up.calculated_score)
                                    FROM user_progress up
                                    JOIN questions q ON up.question_id = q.id
                                    WHERE up.user_id = ?
                                    " . ($category != 'All Categories' ? "AND q.category = '" . $conn->real_escape_string($category) . "'" : "") . "
                                )
                                AND ranked_users.time < (
                                    SELECT SUM(up.response_time_ms)
                                    FROM user_progress up
                                    JOIN questions q ON up.question_id = q.id
                                    WHERE up.user_id = ?
                                    " . ($category != 'All Categories' ? "AND q.category = '" . $conn->real_escape_string($category) . "'" : "") . "
                                )
                            )
                        ) AS better_users
                      FROM accounts u
                      JOIN user_progress up ON u.id = up.user_id
                      JOIN questions q ON up.question_id = q.id
                      LEFT JOIN user_inventory ui ON u.id = ui.user_id AND ui.is_selected = 1
                      LEFT JOIN profile_items pi ON ui.item_id = pi.id
                      WHERE u.id = ?";
        
        if ($category != 'All Categories') {
            $rankQuery .= " AND q.category = '" . $conn->real_escape_string($category) . "'";
        }
        
        $rankQuery .= " GROUP BY u.id";
        
        $stmt = $conn->prepare($rankQuery);
        $stmt->bind_param("iiii", $userId, $userId, $userId, $userId);
        $stmt->execute();
        $rankResult = $stmt->get_result();
        
        if ($rankResult->num_rows > 0) {
            $rankData = $rankResult->fetch_assoc();
            $userRank = [
                'rank' => (int)$rankData['better_users'],
                'name' => $rankData['name'],
                'total_score' => (float)$rankData['total_score'],
                'color_code' => $rankData['color_code']
            ];
        }
        $stmt->close();
    }
    
    echo json_encode([
        'status' => 'success',
        'leaderboard' => $leaderboard,
        'user_rank' => $userRank,
        'debug' => [
            'category' => $category,
            'user_id' => $userId
        ]
    ]);
    
} catch(Exception $e) {
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}

$conn->close();
?>