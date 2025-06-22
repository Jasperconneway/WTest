<?php
// weights.php
require_once 'db.php';
require_once 'users.php';

class Weights {
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

    function getWeights($username) {
        $users = new Users($this->db);
        $userid = $users->getUserId($username);

        // Fetch all weights
        $stmt = $this->conn->prepare("SELECT * FROM users_weight WHERE userid = ?");
        $stmt->bind_param("i", $userid);

        if ($stmt->execute()) {
            $result = $stmt->get_result();
            if ($result->num_rows > 0) {
                while ($row = $result->fetch_assoc()) {
                    echo "Date: " . $row["date"] . "<br>";
                    echo "Weight: " . $row["weight"] . "<br>";
                }
            } else {
                echo json_encode(['error' => 'Failed to fetch weights']);
            }
        }
        
        $stmt->close();
    }

    function getUserWeight($username) {
        $users = new Users($this->db);
        $userid = $users->getUserId($username);

        $stmt = $this->conn->prepare("SELECT DATE(date) AS date, weight FROM users_weight WHERE userid = ? ORDER BY date DESC;");
        $stmt->bind_param("i", $userid);

        if ($stmt->execute()) {
            $result = $stmt->get_result();
            return $result;
        } else {
            return 0;
        }

        return 0;
    }

    function findWeight($date, $weight, $userid) {
        $found = false;
        $stmt = $this->conn->prepare("SELECT * FROM users_weight WHERE date = ? AND weight = ? AND userid = ?");
        $stmt->bind_param("sdi", $date, $weight, $userid);

        if ($stmt->execute()) {
            $found = true;
        } 

        return $found;
    }

    function addWeight($weightid, $date, $weight, $username) {
        $users = new Users($this->db);
        $userid = $users->getUserId($username);

        $stmt = $this->conn->prepare("INSERT INTO users_weight (weightid, date, weight, userid) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("isdi", $weightid, $date, $weight, $userid);

        if ($stmt->execute()) {
            echo "success";
        } else {
            echo "failure";
        }
        
        $stmt->close();
    }

    function getWeightId($date, $weight, $username) {
        $users = new Users($this->db);
        $userid = $users->getUserId($username);
        $stmt = $this->conn->prepare("SELECT weightid FROM users_weight WHERE date = ? AND (ABS(weight - ?) < 0.0001) AND userid = ?");
        $stmt->bind_param("sdi", $date, $weight, $userid);
        
        if ($stmt->execute()) {
            $stmt->bind_result($weightid);

            if ($stmt->fetch()) {
                return $weightid;
            } else {
                return -1;
            }
        } else {
            return -1;
        }   

        $stmt->close();
    }

    function updateWeight($newWeight, $weightid) {
        $Updated = false;
        $stmt = $this->conn->prepare("UPDATE users_weight SET weight = ? WHERE weightid = ?");
        $stmt->bind_param("di", $newWeight, $weightid);

        if ($stmt->execute()) {
            $Updated = true;
        }

        return $Updated;
    }

    function deleteWeight($weightid) {
        $weightDeleted = false;
        $stmt = $this->conn->prepare("DELETE FROM users_weight WHERE weightid = ?");
        $stmt->bind_param("i", $weightid);

        if ($stmt->execute()) {
            if ($stmt->affected_rows > 0) {
                $weightDeleted = true;

            }
        }

        return $weightDeleted;
    }

    function currentUserWeight($username) {
        $users = new Users($this->db);
        $userid = $users->getUserId($username);
        $stmt = $this->conn->prepare("SELECT weight FROM users_weight WHERE userid = ? ORDER BY date DESC LIMIT 1");
        $stmt->bind_param("i", $userid);

        if($stmt->execute()) {
            $stmt->bind_result($weight);

            if ($stmt->fetch()) {
                echo $weight;
            } else {
                echo "-1";
            }
        } else {
            echo "-1";
        }
    }
    
}

?>
