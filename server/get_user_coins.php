<?php
include 'config.php';

header("Content-Type: application/json");

if (!isset($_GET['user_id'])) {
    die(json_encode(["status" => "error", "message" => "User ID required"]));
}

$userId = $conn->real_escape_string($_GET['user_id']);

try {
    $query = "SELECT coins FROM accounts WHERE id = $userId";
    $result = $conn->query($query);
    
    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        echo json_encode([
            "status" => "success",
            "coins" => $user['coins']
        ]);
    } else {
        echo json_encode([
            "status" => "error", 
            "message" => "User not found"
        ]);
    }
    
} catch(Exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>