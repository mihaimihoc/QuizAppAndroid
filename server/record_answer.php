<?php
include 'config.php';

$data = json_decode(file_get_contents("php://input"));

if(isset($data->user_id) && isset($data->question_id) && 
   isset($data->is_correct) && isset($data->response_time_ms) &&
   isset($data->calculated_score)) {
    
    $stmt = $conn->prepare("INSERT INTO user_progress 
                          (user_id, question_id, is_correct, response_time_ms, calculated_score) 
                          VALUES (?, ?, ?, ?, ?)");
    $stmt->bind_param("iiidi", 
        $data->user_id,
        $data->question_id,
        $data->is_correct,
        $data->response_time_ms,
        $data->calculated_score
    );
    
    if($stmt->execute()) {
        echo json_encode(["status" => "success"]);
    } else {
        echo json_encode(["status" => "error", "message" => $conn->error]);
    }
    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "Invalid data"]);
}

$conn->close();
?>