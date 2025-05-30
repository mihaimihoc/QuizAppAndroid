<?php
include 'config.php';

header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"));

if(isset($data->user_id) && isset($data->coins)) {
    $user_id = $conn->real_escape_string($data->user_id);
    $coins = (int)$conn->real_escape_string($data->coins);
    
    // Use atomic update to add coins rather than set absolute value
    $sql = "UPDATE accounts SET coins = coins + ? WHERE id = ?";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ii", $coins, $user_id);
    
    if($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Coins updated"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Error updating coins"]);
    }
    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "Invalid input"]);
}

$conn->close();
?>