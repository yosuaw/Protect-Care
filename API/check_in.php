<?php
error_reporting(E_ERROR | E_PARSE); //Disable error/warning di PHP
$conn = new mysqli("localhost", "native_160419038", "ubaya", "native_160419038");
if($conn->connect_errno) {
    die(json_encode(array('result' => 'ERROR', 'message' => 'Failed to connect to DB '. $conn->connect_error)));
}

if(isset($_POST['username']) && isset($_POST['code']) && isset($_POST['vaccine_count'])) {
    $username = $_POST['username'];
    $vaccine_count = $_POST['vaccine_count'];
    $code = $_POST['code'];   
    date_default_timezone_set("Asia/Bangkok");
    $checkin = date("Y-m-d H:i:s");
    
    $query = "INSERT INTO history(username, vaccine_count, code, check_in) VALUES(?, ?, ?, ?)";
    if($stmt = $conn->prepare($query)) {
        $stmt->bind_param("siss", $username, $vaccine_count, $code, $checkin);
        $success = $stmt->execute();
        
        if($success) {
            $arr = array("result" => "OK");      
        } else {
            $arr = array("result" => "NG");
        }
    } else {
        $arr = array("result" => "NG");
    }
} else {
    $arr = array("result" => "NG");
}

echo json_encode($arr);
?>