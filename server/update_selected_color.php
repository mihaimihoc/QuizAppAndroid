<?php
include 'config.php';

header("Content-Type: application/json");

$userId = isset($_POST['user_id']) ? $_POST['user_id'] : null;
$colorCode = isset($_POST['color_code']) ? $_POST['color_code'] : 'default';

if (!isset($userId) || !is_numeric($userId)) {
    echo json_encode(["status" => "error", "message" => "Invalid user ID"]);
    exit;
}

$userId = $conn->real_escape_string($userId);
$colorCode = $conn->real_escape_string($colorCode);

try {
    $conn->autocommit(FALSE); // Start transaction
    
    // First, reset all selected colors for this user
    $query = "UPDATE user_inventory SET is_selected = 0 WHERE user_id = $userId";
    if (!$conn->query($query)) {
        throw new Exception("Failed to reset colors");
    }
    
    // If not default, set the new selected color
    if ($colorCode !== 'default') {
        $query = "UPDATE user_inventory ui
                  JOIN profile_items pi ON ui.item_id = pi.id
                  SET ui.is_selected = 1
                  WHERE ui.user_id = $userId AND pi.color_code = '$colorCode'";
        
        if (!$conn->query($query)) {
            throw new Exception("Failed to update selected color");
        }
    }
    
    $conn->commit();
    echo json_encode(["status" => "success"]);
    
} catch (Exception $e) {
    $conn->rollback();
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
} finally {
    $conn->autocommit(TRUE);
    $conn->close();
}