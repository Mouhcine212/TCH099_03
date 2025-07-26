<?php
require_once __DIR__ . '/../db/Database.php';
require_once __DIR__ . '/../jwt/utils.php';

header('Content-Type: application/json');

$requestUri = $_SERVER['REQUEST_URI'];
$segments = explode('/', $requestUri);
$id = end($segments);

// Vérifie si l'id est numérique
if (!is_numeric($id)) {
    http_response_code(400);
    echo json_encode(['error' => 'ID manquant ou invalide']);
    exit;
}

try {
    $cnx = Database::getInstance();
    $stmt = $cnx->prepare("SELECT * FROM VOLS WHERE ID_VOL = :id");
    $stmt->bindParam(':id', $id, PDO::PARAM_INT);
    $stmt->execute();

    $vol = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($vol) {
        echo json_encode($vol);
    } else {
        http_response_code(404);
        echo json_encode(['error' => 'Vol introuvable']);
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur serveur']);
}
