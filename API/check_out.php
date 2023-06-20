<?php
error_reporting(E_ERROR | E_PARSE); //Disable error/warning di PHP
$conn = new mysqli("localhost", "native_160419038", "ubaya", "native_160419038");
if($conn->connect_errno) {
    die(json_encode(array('result' => 'ERROR', 'message' => 'Failed to connect to DB '. $conn->connect_error)));
}

if(isset($_POST['username']) && isset($_POST['code'])) {
    date_default_timezone_set("Asia/Bangkok");
    $username = $_POST['username'];
    $code = $_POST['code'];      
    $checkout = date("Y-m-d H:i:s");
    
    $query = "UPDATE history SET check_out = ? WHERE username = ? AND code = ? AND check_out IS NULL";
    if($stmt = $conn->prepare($query)) {
        $stmt->bind_param("sss", $checkout, $username, $code);
        $stmt->execute();
        
        if($stmt-> affected_rows > 0) {
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