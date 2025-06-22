<?php
// login.php

if (isset($_POST['username']) && isset($_POST['password'])) {
    require_once 'users.php';
    require_once 'weights.php';
    require_once 'validate.php';

    $username = validate($_POST['username']);
    $password = validate($_POST['password']);

    $db = new Db;
    $users = new Users($db);
    
    if ($users->getUser($username, $password)) {
        echo "success";
    } else {
        echo "failure";
    }

}

?>