<?php
// users.php
require_once 'db.php';
require_once 'users.php';

class Users {
    public $stmt;
    public $result;
    private $db;
    private $conn;

    function __construct(Db $db) {
        $this->stmt = null;
        $this->result = null;
        $this->db = $db;
        $this->conn = $this->db->connection;
    }

    private function getUsers() {
        $result = $this->conn->query("SELECT * FROM users");
        
        if ($result->num_rows > 0) {
            while ($row = $result->fetch_assoc()) {
                echo "Username: " . $row["username"] . "<br>";
                echo "Password: " . $row["password"] . "<br>";
                echo "Phone Number: " . $row["number"] . "<br>";
            }
        } else {
            echo json_encode(['error' => 'Failed to fetch users']);
        }
    }

    function getUser($username, $password) {
        $added = false;
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ? AND password = ?");
        $stmt->bind_param("ss", $username, $password);

        if($stmt->execute()) {
            $result = $stmt->get_result();

            if ($result->num_rows > 0) {
                $added = true;
            } else {
                $added = false;
            }
        }

        return $added;
    }

    function getUserId($username) { // fix checks to make sure not -1
        $stmt = $this->conn->prepare("SELECT id FROM users WHERE username = ?");
        $stmt->bind_param("s", $username);

        if ($stmt->execute()) {
            $stmt->bind_result($userid);

            if ($stmt->fetch()) {
                return $userid;
            }
        } else {
            return -1;
        }

        $stmt->close();
    }

    function addUser($username, $password, $number) {
        $stmt = $this->conn->prepare("INSERT INTO users (username, password, number) VALUES (?, ?, ?)");
        $stmt->bind_param("sss", $username, $password, $number);

        if ($stmt->execute()) {
            echo "success";
        } else {
            echo "failure";
        }
        
        $stmt->close();
    }

    function deleteUser($id) {
        $stmt = $this->conn->prepare("DELETE FROM users WHERE id = ?");
        $stmt->bind_param("i", $id);

        if ($stmt->execute()) {
            if ($stmt->affected_rows > 0) {
                echo "success";
            } else {
                echo "failure";
            }
            
        } else {
            echo "failure";
        }
        
        $stmt->close();
    }

    function numberExists($number) {
        $found = false;

        $stmt = $this->conn->prepare("SELECT number FROM users WHERE number = ?");
        $stmt->bind_param("s", $number);

        if ($stmt->execute()) {
            $result = $stmt->get_result();
            if ($result->num_rows > 0) {
                $found = true;
            }
        }
        return $found;
    }

    function usernameExists($username) {
        $found = false;

        $stmt = $this->conn->prepare("SELECT username FROM users WHERE username = ?");
        $stmt->bind_param("s", $username);

        if ($stmt->execute()) {
            $result = $stmt->get_result();
            if ($result->num_rows > 0) {
                $found = true;
            }
        }
        return $found;
    }

    function userExists($username, $number) {
        $found = false;

        $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ? AND number = ?");
        $stmt->bind_param("ss", $username, $number);

        if ($stmt->execute()) {
            $result = $stmt->get_result();
            if ($result->num_rows > 0) {
                $found = true;
            }
        }
        return $found;
    }
}

?>