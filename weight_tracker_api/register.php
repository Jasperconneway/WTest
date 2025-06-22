<?php
// register.php

if (isset($_POST['username']) && isset($_POST['password']) && isset($_POST['number'])) {
    require_once 'users.php';
    require_once 'weights.php';
    require_once 'validate.php';

    $username = validate($_POST['username']);
    $password = validate($_POST['password']);
    $number = validate($_POST['number']);

    $db = new Db;
    $users = new Users($db);

    if ($users->userExists($username, $number)) {
        echo "User account exists";
    } elseif ($users->usernameExists($username)) {
        echo "Username exists";
    } elseif ($users->numberExists($number)) {
        echo "Number exists";
    } else {
        $users->addUser($username, $password, $number);
    }

}

?>