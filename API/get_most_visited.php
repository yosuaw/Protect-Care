<?php
error_reporting(E_ERROR | E_PARSE); //Disable error/warning di PHP
$conn = new mysqli("localhost", "native_160419038", "ubaya", "native_160419038");
if($conn->connect_errno) {
    die(json_encode(array('result' => 'ERROR', 'message' => 'Failed to connect to DB '. $conn->connect_error)));
}

if (isset($_POST['username'])) {
    $username = $_POST['username'];
    $query = "SELECT p.name as place, COUNT(h.code) as jumlah, p.image as image FROM history h INNER JOIN place p ON h.code = p.code WHERE username = ? GROUP BY h.code ORDER BY COUNT(h.code) DESC LIMIT 1";
    
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $result = $stmt->get_result();

    if($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $data = array("place" => $row['place'], "count" => $row['jumlah'], "image" => $row['image']);
      
        echo json_encode(array('result' => 'OK', 'data' => $data));
    } else {
        die(json_encode(array('result' => 'ERROR', 'message' => 'No data found')));
    }
} else {
    $arr = array("result" => "NG", "message" => "No username");
}
?>