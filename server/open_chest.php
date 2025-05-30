<?php
include 'config.php';

header("Content-Type: application/json");

// Allow both POST and GET (GET for testing only — not secure for production)
$userId = isset($_POST['user_id']) ? $_POST['user_id'] : (isset($_GET['user_id']) ? $_GET['user_id'] : null);
$chestId = isset($_POST['chest_id']) ? $_POST['chest_id'] : (isset($_GET['chest_id']) ? $_GET['chest_id'] : 1);

// Validate user ID
if (!isset($userId) || !is_numeric($userId)) {
    echo json_encode(["status" => "error", "message" => "Invalid user ID"]);
    exit;
}

$userId = $conn->real_escape_string($userId);
$chestId = $conn->real_escape_string($chestId);

try {
    $conn->autocommit(FALSE); // Start transaction

    // Lock user's row
    $query = "SELECT coins FROM accounts WHERE id = $userId FOR UPDATE";
    $result = $conn->query($query);

    if ($result->num_rows === 0) {
        throw new Exception("User not found");
    }

    $user = $result->fetch_assoc();
    
    // Get chest data
    $query = "SELECT * FROM chests WHERE id = $chestId";
    $result = $conn->query($query);

    if ($result->num_rows === 0) {
        throw new Exception("Invalid chest");
    }

    $chest = $result->fetch_assoc();

    if ($user['coins'] < $chest['price']) {
        throw new Exception("Not enough coins");
    }

    // Deduct coins FIRST (always happens)
    $newCoins = $user['coins'] - $chest['price'];
    $query = "UPDATE accounts SET coins = $newCoins WHERE id = $userId";
    if (!$conn->query($query)) {
        throw new Exception("Failed to update coins");
    }

    // Determine rarity
    $random = mt_rand(1, 10000) / 100;
    $rarity = 'common';
    if ($random <= $chest['legendary_prob']) {
        $rarity = 'legendary';
    } elseif ($random <= $chest['epic_prob'] + $chest['legendary_prob']) {
        $rarity = 'epic';
    } elseif ($random <= $chest['rare_prob'] + $chest['epic_prob'] + $chest['legendary_prob']) {
        $rarity = 'rare';
    }

    // Select random item
    $query = "SELECT * FROM profile_items WHERE rarity = '$rarity' ORDER BY RAND() LIMIT 1";
    $result = $conn->query($query);

    if ($result->num_rows === 0) {
        throw new Exception("No items of this rarity available");
    }

    $item = $result->fetch_assoc();

    // Check if user already has this item
    $checkQuery = "SELECT COUNT(*) FROM user_inventory WHERE user_id = $userId AND item_id = {$item['id']}";
    $checkResult = $conn->query($checkQuery);
    $itemExists = ($checkResult->fetch_row()[0] > 0);

    $itemAdded = false;

    // Only add to inventory if it doesn't exist
    if (!$itemExists) {
        $query = "INSERT INTO user_inventory (user_id, item_id) VALUES ($userId, {$item['id']})";
        if (!$conn->query($query)) {
            throw new Exception("Failed to add item to inventory");
        }
        $itemAdded = true;
    }

    $conn->commit();

    echo json_encode([
        "status" => "success",
        "data" => [
            "name" => $item['name'],
            "color_code" => $item['color_code'],
            "rarity" => $item['rarity'],
            "remaining_coins" => $newCoins,
            "is_new" => $itemAdded, // Tells Android whether the item was added
        ]
    ]);

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