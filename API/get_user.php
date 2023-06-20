<?php
    error_reporting(E_ERROR || E_PARSE);
    $conn = new mysqli("localhost", "native_160419038", "ubaya", "native_160419038");
    //Check any connection errors
    if ($conn->connect_error) {
        die(json_encode(array('result' => 'ERROR', 'message'=> 'Failed to connect to DB ' . $conn->connect_error)));
    }
    $username = $_POST['username'];
    $conn->set_charset("UTF8");
    $query = "SELECT * FROM users WHERE username = ?";
    if ($stmt = $conn->prepare($query)) {
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();

        $data = array();

        if ($result->num_rows > 0) {
            while ($obj = $result->fetch_assoc()) {
                $data[] = $obj;
            }
            echo json_encode(array('result' => 'OK', 'data' => $data));
        } else {
            die(json_encode(array('result' => 'ERROR', 'message' => 'No data found')));
        }
    } else {
        //Prepare statement fails
        $arr = array(
            "result" => "NG",
            "message" => "Unable to prepare user prepare statement fails"
        );
    }
?>