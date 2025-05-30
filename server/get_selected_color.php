<?php
include 'config.php';

header("Content-Type: application/json");

$userId = isset($_GET['user_id']) ? $_GET['user_id'] : null;

if (!isset($userId) || !is_numeric($userId)) {
    echo json_encode(["status" => "error", "message" => "Invalid user ID"]);
    exit;
}

$userId = $conn->real_escape_string($userId);

try {
    $query = "SELECT pi.color_code 
              FROM user_inventory ui
              JOIN profile_items pi ON ui.item_id = pi.id
              WHERE ui.user_id = $userId AND ui.is_selected = 1
              LIMIT 1";
    
    $result = $conn->query($query);
    
    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        echo json_encode([
            "status" => "success",
            "color_code" => $row['color_code']
        ]);
    } else {
        echo json_encode([
            "status" => "success",
            "color_code" => "default"
        ]);
    }
    
} catch (Exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
} finally {
    $conn->close();
}