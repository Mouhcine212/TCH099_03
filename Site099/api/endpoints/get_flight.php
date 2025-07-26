<?php
require_once '../db/Database.php';
header('Content-Type: application/json');

if (!isset($_GET['id_vol'])) {
    http_response_code(400);
    echo json_encode(['error' => 'ID vol manquant']);
    exit();
}

try {
    $db = Database::getInstance();
    $stmt = $db->prepare("SELECT * FROM VOLS WHERE ID_VOL = ?");
    $stmt->execute([$_GET['id_vol']]);
    echo json_encode($stmt->fetch(PDO::FETCH_ASSOC));
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur serveur']);
}
