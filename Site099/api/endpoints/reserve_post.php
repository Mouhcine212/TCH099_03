<?php
require_once __DIR__ . '/../db/Database.php';
require_once __DIR__ . '/../jwt/utils.php';


header('Content-Type: application/json');

// Authentification
$headers = apache_request_headers();
if (!isset($headers['Authorization'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Token manquant']);
    exit();
}

$jwt = str_replace('Bearer ', '', $headers['Authorization']);
$decoded = decodeJWT($jwt);

if (!$decoded) {
    http_response_code(403);
    echo json_encode(['error' => 'Token invalide']);
    exit();
}

$data = json_decode(file_get_contents('php://input'), true);
if (!isset($data['flight_id']) || !isset($data['seat_number'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit();
}

try {
    $db = Database::getInstance();

    // Insertion dans la table RESERVATIONS
    $stmt = $db->prepare("INSERT INTO RESERVATIONS (id_utilisateur, id_vol, numero_siege) VALUES (:id_utilisateur, :id_vol, :siege)");
    $stmt->execute([
        ':id_utilisateur' => $decoded->[id],
        ':id_vol' => $data['flight_id'],
        ':siege' => $data['seat_number']
    ]);

    echo json_encode(['success' => true]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur serveur : '.$e->getMessage()]);
}
