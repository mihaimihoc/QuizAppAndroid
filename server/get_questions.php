<?php
include 'config.php';

header("Content-Type: application/json");

try {
    $userId = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;
    
    $query = "SELECT q.*, 
              up.is_correct,
              up.calculated_score
              FROM questions q
              LEFT JOIN user_progress up ON q.id = up.question_id AND up.user_id = $userId";
              
    $result = $conn->query($query);
    
    $questions = array();
    while($row = $result->fetch_assoc()) {
        $questions[] = $row;
    }
    
    echo json_encode([
        "status" => "success",
        "data" => $questions
    ]);
    
} catch(Exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>