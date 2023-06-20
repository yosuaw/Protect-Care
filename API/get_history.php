<?php
error_reporting(E_ERROR | E_PARSE); //Disable error/warning di PHP
$conn = new mysqli("localhost", "native_160419038", "ubaya", "native_160419038");
if($conn->connect_errno) {
    die(json_encode(array('result' => 'ERROR', 'message' => 'Failed to connect to DB '. $conn->connect_error)));
}

if (isset($_POST['username'])) {
    $username = $_POST['username'];
    $query = "SELECT h.username as username, h.vaccine_count as vaccine_count, p.name as location, h.check_in as check_in, h.check_out as check_out FROM history h INNER JOIN place p ON h.code = p.code WHERE h.username = ? ORDER BY h.check_in DESC";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $result = $stmt->get_result();
    $data = array();

    if($result->num_rows > 0) {
        while($obj = $result->fetch_object()) {
            $data[] = $obj;
        }
        echo json_encode(array('result' => 'OK', 'data' => $data));
    } else {
        die(json_encode(array('result' => 'ERROR', 'message' => 'No data found')));
    }
} else {
    $arr = array("result" => "NG", "message" => "No username");
}
?>