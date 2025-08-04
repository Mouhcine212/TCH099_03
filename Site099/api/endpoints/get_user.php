<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Authorization, Content-Type');

require_once __DIR__ . '/../db/config.php';
require_once __DIR__ . '/../jwt/utils.php';

$headers = getallheaders();

if (!isset($headers['Authorization'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Token manquant']);
    exit;
}

$authHeader = $headers['Authorization'];
list($type, $token) = explode(' ', $authHeader, 2);

if (strtolower($type) !== 'bearer' || !$token) {
    http_response_code(401);
    echo json_encode(['error' => 'Token invalide']);
    exit;
}

$decoded = verifyJWT($token);
if (!$decoded || !isset($decoded->user_id)) {
    http_response_code(401);
    echo json_encode(['error' => 'JWT invalide']);
    exit;
}

$user_id = $decoded->user_id;

$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur connexion DB']);
    exit;
}

$sql = "SELECT NOM FROM UTILISATEURS WHERE ID_UTILISATEUR = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param('i', $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode([
        'id_utilisateur' => $user_id,
        'nom' => $row['NOM']
    ]);
} else {
    http_response_code(404);
    echo json_encode(['error' => 'Utilisateur introuvable']);
}

$stmt->close();
$conn->close();

