<?php
error_reporting(E_ERROR | E_PARSE); //Disable error/warning di PHP
$conn = new mysqli("localhost", "native_160419038", "ubaya", "native_160419038");
if($conn->connect_errno) {
    die(json_encode(array('result' => 'ERROR', 'message' => 'Failed to connect to DB '. $conn->connect_error)));
}

$query = "SELECT * FROM place";
$result = $conn->query($query);
$data = array();

if($result->num_rows > 0) {
    while($obj = $result->fetch_object()) {
        $data[] = $obj;
    }
    echo json_encode(array('result' => 'OK', 'data' => $data));
} else {
    die(json_encode(array('result' => 'ERROR', 'message' => 'No data found')));
}
?>