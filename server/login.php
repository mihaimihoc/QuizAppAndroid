<?php
include 'config.php';

$data = json_decode(file_get_contents("php://input"));

if(isset($data->email) && isset($data->password)) {
    $email = $conn->real_escape_string($data->email);
    $password = $conn->real_escape_string($data->password);
    
    $sql = "SELECT * FROM accounts WHERE email = '$email'";
    $result = $conn->query($sql);
    
    if($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        if(password_verify($password, $user['password'])) {
            echo json_encode(["status" => "success", "message" => "Login successful", "user" => [
                "id" => $user['id'],
                "name" => $user['name'],
                "email" => $user['email'],
                "coins" => $user['coins'] // Make sure your accounts table has a coins column
            ]]);
        } else {
            echo json_encode(["status" => "error", "message" => "Invalid password"]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "User not found"]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Invalid input"]);
}

$conn->close();
?>