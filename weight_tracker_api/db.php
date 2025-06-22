<?php
// db.php
class db {
    private $host = 'localhost';
    private $dbname = 'weight_tracker';
    private $username = 'root';
    private $password = '';
    public $connection;

    function __construct() {
        // Database connection
        $this->connection = new mysqli($this->host, $this->username, $this->password, $this->dbname);

        // Check connection for error
        if ($this->connection->connect_error) {
            die("Connection failed: " . $this->connection->connect_error);
        }
    }
}

?>
