<?php
error_reporting(E_ERROR | E_PARSE); //Disable error/warning di PHP
$conn = new mysqli("localhost", "native_160419038", "ubaya", "native_160419038");
if($conn->connect_errno) {
    die(json_encode(array('result' => 'ERROR', 'message' => 'Failed to connect to DB '. $conn->connect_error)));
}

if(isset($_POST['username']) && isset($_POST['password'])) {
    $username = $_POST['username'];
    $password = $_POST['password'];

    $query = "SELECT * FROM users WHERE username = ?";
    if($stmt = $conn->prepare($query)) {
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            
            if($row['password'] == $password) {
                $arr = array("result" => "OK", "message" => "Login successful!");     
            } else {
                $arr = array("result" => "NG", "message" => "Incorrect password!");    
            }      
        } else {
            $arr = array("result" => "NG", "message" => "Username not found!");
        }
    } else {
        $arr = array("result" => "NG", "message" => "Login failed!");
    }
} else {
    $arr = array("result" => "NG", "message" => "Login failed!");
}

echo json_encode($arr);
?>