<?php
include 'config.php';

header("Content-Type: application/json");

if(!isset($_GET['user_id'])) {
    die(json_encode(["status" => "error", "message" => "User ID required"]));
}

$userId = $conn->real_escape_string($_GET['user_id']);

try {
    $query = "SELECT q.category, COUNT(up.id) as answered, 
              SUM(up.is_correct) as correct 
              FROM user_progress up
              JOIN questions q ON up.question_id = q.id
              WHERE up.user_id = $userId
              GROUP BY q.category";
              
    $result = $conn->query($query);
    
    $progress = array();
    while($row = $result->fetch_assoc()) {
        $progress[] = $row;
    }
    
    echo json_encode([
        "status" => "success",
        "data" => $progress
    ]);
    
} catch(Exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>