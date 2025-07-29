<?php
require_once(__DIR__ . '/../db/Database.php');
require_once(__DIR__ . '/../jwt/utils.php');

header('Content-Type: application/json');

$headers = apache_request_headers();
if (!isset($headers['Authorization'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Token manquant']);
    exit();
}

$jwt = str_replace('Bearer ', '', $headers['Authorization']);
$decoded = decodeJWT($jwt);

if (!$decoded || !isset($decoded['id'])) {
    http_response_code(403);
    echo json_encode(['error' => 'Token invalide']);
    exit();
}

$data = json_decode(file_get_contents('php://input'), true);
$flight_id = $data['id_vol'] ?? null;
$seat_number = $data['numero_siege'] ?? null;
$date_naissance = $data['date_naissance'] ?? null;
$numero_passeport = $data['numero_passeport'] ?? null;

if (!$flight_id || !$seat_number || !$date_naissance || !$numero_passeport) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit();
}

try {
    $db = Database::getInstance();
    $db->beginTransaction();

    $stmt = $db->prepare("
        INSERT INTO RESERVATIONS (ID_UTILISATEUR, ID_VOL, NUMERO_SIEGE)
        VALUES (:id_utilisateur, :id_vol, :siege)
    ");
    $stmt->execute([
        ':id_utilisateur' => $decoded['id'],
        ':id_vol' => $flight_id,
        ':siege' => $seat_number
    ]);

    $id_reservation = $db->lastInsertId();

    $nom_complet = $decoded['nom'] ?? 'Passager inconnu';

    $stmt2 = $db->prepare("
        INSERT INTO PASSAGERS (ID_RESERVATION, NOM_COMPLET, DATE_NAISSANCE, NUMERO_PASSEPORT)
        VALUES (:id_res, :nom, :naissance, :passeport)
    ");
    $stmt2->execute([
        ':id_res' => $id_reservation,
        ':nom' => $nom_complet,
        ':naissance' => $date_naissance,
        ':passeport' => $numero_passeport
    ]);

    $db->commit();
    echo json_encode(['success' => true]);
} catch (Exception $e) {
    $db->rollBack();
    http_response_code(500);
    echo json_encode(['error' => 'Erreur serveur : ' . $e->getMessage()]);
}
