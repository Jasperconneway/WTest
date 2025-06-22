<?php
// fetchData.php
require_once 'users.php';
require_once 'weights.php';
require_once 'validate.php';


// Handle API requests
$action = $_GET['action'] ?? '';


if ($action === 'getUserWeight') {
    ob_start();
    ini_set('display_errors', 1);
    error_reporting(E_ALL);
    header('Content-Type: application/json');
    $username = validate($_POST['username']);

    // Database connection
    $db = new Db();

    // Weights API
    $weightsApi = new Weights($db);

    $result = $weightsApi->getUserWeight($username);


    if ($result->num_rows > 0) {
        $data = array();

        // Fetch rows as associative array
        while ($row = $result->fetch_assoc()) {
            $data[] = $row;
        }
        // Return JSON-encoded data
        echo json_encode($data);
    } else {
        echo json_encode(array('error' => 'Failed to fetch weights'));
    }
    
    ob_end_flush();
} elseif ($action === 'addWeight') {
    $weightid = rand(0, 9999);
    $date = $_POST['date'] ?? '';
    $weight = $_POST['weight'] ?? '';
    $username = validate($_POST['username']);

    // Database connection
    $db = new Db();

    // Weights API
    $weightsApi = new Weights($db);

    $weightsApi->addWeight($weightid, $date, $weight, $username);

} elseif ($action === 'getWeightId') {
    $date = $_POST['date'] ?? '';
    $weight = $_POST['weight'] ?? 0;
    $username = validate($_POST['username']);

    // Database connection
    $db = new Db();

    // Weights API
    $weightsApi = new Weights($db);

    $weightsApi->getWeightId($date, $weight, $username);

} elseif ($action === 'updateWeight') {
    $date = $_POST['date'] ?? '';
    $weight = $_POST['weight'] ?? 0;
    $username = validate($_POST['username']);
    $updatedWeight = $_POST['updatedWeight'] ?? 0;
    //$weightid = $_POST['weightid'] ?? 0;

    // Database connection
    $db = new Db();

    // Weights API
    $weightsApi = new Weights($db);

    $weightid = $weightsApi->getWeightId($date, $weight, $username);

    if ($weightid == "-1") {
        echo "failure";
    } else {
        if ($weightsApi->updateWeight($updatedWeight, $weightid)) {
            echo "success";
        } else {
            echo "failure";
        }
    }
} elseif ($action === 'deleteWeight') {
    $date = $_POST['date'] ?? '';
    $weight = $_POST['weight'] ?? 0;
    $username = validate($_POST['username']);
    //$weightid = $_POST['weightid'] ?? 0;

    // Database connection
    $db = new Db();

    // Weights API
    $weightsApi = new Weights($db);

    $weightid = $weightsApi->getWeightId($date, $weight, $username);

    if ($weightid == "-1") {
        echo "failure";
    } else {
        if ($weightsApi->deleteWeight($weightid)) {
            echo "success";
        } else {
            echo "failure";
        }
    }
} elseif ($action === 'currentUserWeight') {
    $username = validate($_POST['username']);

    // Database connection
    $db = new Db();

    // Weights API
    $weightsApi = new Weights($db);

    $weightsApi->currentUserWeight($username);
    
} elseif ($action === 'getUserId') {
    // Fetch specific user
    $username = validate($_POST['username']);

    // Database connection
    $db = new Db();

    // Users API for users access
    $usersApi = new Users($db);

    $usersApi->getUserId($username);

} elseif ($action === 'deleteUser') {
    // Delete a user
    $username = $_POST['username'] ?? '';
    
    // Database connection
    $db = new Db();

    // Users API for users access
    $usersApi = new Users($db);

    $id = $usersApi->getUserId($username);

    $usersApi->deleteUser($id);

} else {
    echo json_encode(['error' => 'Invalid action']);
}

?>