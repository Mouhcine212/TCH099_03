<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Authorization, Content-Type');

require_once __DIR__ . '/../db/config.php';
require_once __DIR__ . '/../jwt/utils.php';

$headers = getallheaders();
$authHeader = $headers['Authorization'] ?? '';

if (!$authHeader || !str_starts_with($authHeader, 'Bearer ')) {
    http_response_code(401);
    echo json_encode(['error' => 'Token manquant']);
    exit();
}

$token = substr($authHeader, 7);
$userData = verify_jwt($token);

if (!$userData || (!isset($userData['id']) && !isset($userData['id_utilisateur']))) {
    http_response_code(401);
    echo json_encode(['error' => 'Token invalide']);
    exit();
}

$idUtilisateur = $userData['id_utilisateur'] ?? $userData['id'];

$data = json_decode(file_get_contents("php://input"), true);
$idReservation = $data['id_reservation'] ?? null;

if (!$idReservation) {
    http_response_code(400);
    echo json_encode(['error' => 'ID de réservation manquant']);
    exit();
}

$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur de connexion DB']);
    exit();
}

$stmt = $conn->prepare("
    SELECT r.ID_RESERVATION, pay.ID_PAIEMENT, pay.STATUT AS STATUT_PAIEMENT
    FROM RESERVATIONS r
    LEFT JOIN PAIEMENTS pay ON pay.ID_RESERVATION = r.ID_RESERVATION
    WHERE r.ID_RESERVATION = ? AND r.ID_UTILISATEUR = ?
");
$stmt->bind_param('ii', $idReservation, $idUtilisateur);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    http_response_code(403);
    echo json_encode(['error' => 'Réservation introuvable ou non autorisée']);
    exit();
}

$reservation = $result->fetch_assoc();
$stmt->close();

$stmt = $conn->prepare("UPDATE RESERVATIONS SET STATUT = 'Annulée' WHERE ID_RESERVATION = ?");
$stmt->bind_param('i', $idReservation);
$stmt->execute();
$stmt->close();

if ($reservation['STATUT_PAIEMENT'] === 'Payé' && $reservation['ID_PAIEMENT']) {
    $stmt = $conn->prepare("UPDATE PAIEMENTS SET STATUT = 'Remboursé' WHERE ID_PAIEMENT = ?");
    $stmt->bind_param('i', $reservation['ID_PAIEMENT']);
    $stmt->execute();
    $stmt->close();
    $paiementStatut = 'Remboursé';
} else {
    $paiementStatut = $reservation['STATUT_PAIEMENT'] ?? 'Non payé';
}

echo json_encode([
    'success' => true,
    'id_reservation' => $idReservation,
    'statut_reservation' => 'Annulée',
    'statut_paiement' => $paiementStatut
]);

$conn->close();
