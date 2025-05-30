<?php
include 'config.php';

$data = json_decode(file_get_contents("php://input"));

if(isset($data->email) && isset($data->name) && isset($data->password)) {
    $email = $conn->real_escape_string($data->email);
    $name = $conn->real_escape_string($data->name);
    $password = password_hash($conn->real_escape_string($data->password), PASSWORD_BCRYPT);
    
    $sql = "INSERT INTO accounts (email, name, password) VALUES ('$email', '$name', '$password')";
    
    if($conn->query($sql) === TRUE) {
        echo json_encode(["status" => "success", "message" => "User registered successfully"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Registration failed: " . $conn->error]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Invalid input"]);
}

$conn->close();
?>