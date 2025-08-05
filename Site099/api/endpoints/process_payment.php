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

$decoded = verify_jwt($token);
if (!$decoded || !isset($decoded['id'])) {
    http_response_code(401);
    echo json_encode(['error' => 'JWT invalide']);
    exit;
}

$user_id = $decoded['id'];

$data = json_decode(file_get_contents("php://input"), true);
$id_reservation = $data['id_reservation'] ?? null;
$montant = $data['montant'] ?? null;
$methode = $data['methode'] ?? 'Carte';

if (!$id_reservation || !$montant) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit;
}

$conn = mysqli_init();
mysqli_ssl_set($conn, NULL, NULL, NULL, NULL, NULL);

if (!mysqli_real_connect(
    $conn,
    DB_HOST,
    DB_USER,
    DB_PASS,
    DB_NAME,
    3306,
    NULL,
    MYSQLI_CLIENT_SSL
)) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur connexion DB SSL: ' . mysqli_connect_error()]);
    exit;
}

$stmt = $conn->prepare("SELECT ID_RESERVATION FROM RESERVATIONS WHERE ID_RESERVATION = ? AND ID_UTILISATEUR = ?");
$stmt->bind_param('ii', $id_reservation, $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    http_response_code(404);
    echo json_encode(['error' => 'Réservation introuvable ou non autorisée']);
    $stmt->close();
    $conn->close();
    exit;
}
$stmt->close();

$stmt = $conn->prepare("UPDATE RESERVATIONS SET STATUT = 'Confirmée' WHERE ID_RESERVATION = ?");
$stmt->bind_param('i', $id_reservation);
$stmt->execute();
$stmt->close();

$stmt = $conn->prepare("
    INSERT INTO PAIEMENTS (ID_RESERVATION, DATE_PAIEMENT, MONTANT, METHODE, STATUT)
    VALUES (?, NOW(), ?, ?, 'Payé')
");
$stmt->bind_param('ids', $id_reservation, $montant, $methode);

if ($stmt->execute()) {
    echo json_encode(['success' => true, 'id_reservation' => $id_reservation]);
} else {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur lors de l\'insertion du paiement']);
}

$stmt->close();
$conn->close();
?>
