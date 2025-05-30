<?php
include 'config.php';

header("Content-Type: application/json");

if(!isset($_GET['id'])) {
    die(json_encode(["status" => "error", "message" => "Question ID required"]));
}

$questionId = $conn->real_escape_string($_GET['id']);

try {
    $query = "SELECT * FROM questions WHERE id = $questionId";
    $result = $conn->query($query);
    
    if($result->num_rows > 0) {
        $question = $result->fetch_assoc();
        echo json_encode([
            "status" => "success",
            "data" => $question
        ]);
    } else {
        echo json_encode([
            "status" => "error", 
            "message" => "Question not found"
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