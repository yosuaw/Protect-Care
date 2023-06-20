<?php
error_reporting(E_ERROR | E_PARSE); //Disable error/warning di PHP
$conn = new mysqli("localhost", "native_160419038", "ubaya", "native_160419038");
if($conn->connect_errno) {
    die(json_encode(array('result' => 'ERROR', 'message' => 'Failed to connect to DB '. $conn->connect_error)));
}

if(isset($_POST['username'])) {
    $username = $_POST['username'];

    $query = "SELECT p.name as name, h.vaccine_count as vaccine_count, h.code as code, h.check_in as check_in FROM history h INNER JOIN place p ON h.code = p.code WHERE h.username = ? AND h.check_out IS NULL";
    if($stmt = $conn->prepare($query)) {
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $data = array("place" => $row['name'], "vaccine_count" => $row['vaccine_count'], "code" => $row['code'], "check_in" => $row['check_in']);
            $arr = array("result" => "OK", "data" => $data);      
        } else {
            $arr = array("result" => "NG", "message" => "History not found!");
        }
    } else {
        $arr = array("result" => "NG", "message" => "Prepare error");
    }
} else {
    $arr = array("result" => "NG", "message" => "No data found!");
}

echo json_encode($arr);
?>