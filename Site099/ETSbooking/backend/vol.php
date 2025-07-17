<?php
header("Content-Type: application/json");

$destination = $_GET['destination'];
$date = $_GET['date'];

$conn = new mysqli("localhost", "root", "", "tch099"); // adapte le nom DB

$sql = "SELECT * FROM vols WHERE destination = ? AND date = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $destination, $date);
$stmt->execute();

$result = $stmt->get_result();
$vols = [];

while ($row = $result->fetch_assoc()) {
    $vols[] = $row;
}

echo json_encode($vols);
?>
