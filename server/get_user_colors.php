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
    $query = "SELECT pi.color_code, ui.is_selected 
              FROM user_inventory ui
              JOIN profile_items pi ON ui.item_id = pi.id
              WHERE ui.user_id = $userId";
    
    $result = $conn->query($query);
    $colors = [];
    
    while ($row = $result->fetch_assoc()) {
        $colors[] = $row;
    }
    
    echo json_encode([
        "status" => "success",
        "colors" => $colors
    ]);
    
} catch (Exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
} finally {
    $conn->close();
}