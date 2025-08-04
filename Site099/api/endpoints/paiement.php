<?php
require_once(__DIR__ . '/../db/Database.php');
require_once(__DIR__ . '/../utils/jwt.php');

header('Content-Type: application/json');

$headers = getallheaders();
$authHeader = $headers['Authorization'] ?? '';

if (!$authHeader || !str_starts_with($authHeader, 'Bearer ')) {
    http_response_code(401);
    echo json_encode(['error' => 'Token manquant']);
    exit();
}

$token = substr($authHeader, 7);
$userData = verify_jwt($token);
if (!$userData || !isset($userData['id_utilisateur'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Token invalide']);
    exit();
}

$idUtilisateur = $userData['id_utilisateur'];

$data = json_decode(file_get_contents("php://input"), true);
if (!$data || !isset($data['id_reservation'])) {
    http_response_code(400);
    echo json_encode(['error' => 'ID réservation manquant']);
    exit();
}

$idReservation = intval($data['id_reservation']);
$numCarte = $data['numCarte'] ?? null;
$expCarte = $data['expCarte'] ?? null;
$cvvCarte = $data['cvvCarte'] ?? null;

if (!$numCarte || !$expCarte || !$cvvCarte) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs de paiement manquants']);
    exit();
}

try {
    $cnx = Database::getInstance();
    $cnx->beginTransaction();

    $checkRes = $cnx->prepare("
        SELECT r.ID_RESERVATION, r.STATUT, v.PRIX 
        FROM RESERVATIONS r
        INNER JOIN VOLS v ON r.ID_VOL = v.ID_VOL
        WHERE r.ID_RESERVATION = ? AND r.ID_UTILISATEUR = ?
        LIMIT 1
    ");
    $checkRes->execute([$idReservation, $idUtilisateur]);
    $reservation = $checkRes->fetch(PDO::FETCH_ASSOC);

    if (!$reservation) {
        http_response_code(404);
        echo json_encode(['error' => 'Réservation introuvable']);
        exit();
    }

    if ($reservation['STATUT'] === 'Confirmée') {
        http_response_code(400);
        echo json_encode(['error' => 'Cette réservation est déjà confirmée.']);
        exit();
    }

    $montant = $reservation['PRIX'];

    $pstmt = $cnx->prepare("
        INSERT INTO PAIEMENTS (ID_RESERVATION, DATE_PAIEMENT, MONTANT, METHODE, STATUT)
        VALUES (?, NOW(), ?, 'Carte', 'Payé')
    ");
    $pstmt->execute([$idReservation, $montant]);

    $updateRes = $cnx->prepare("
        UPDATE RESERVATIONS SET STATUT = 'Confirmée' WHERE ID_RESERVATION = ?
    ");
    $updateRes->execute([$idReservation]);

    $cnx->commit();

    echo json_encode([
        'success' => true,
        'message' => 'Paiement effectué et réservation confirmée.',
        'id_reservation' => $idReservation,
        'montant' => $montant
    ]);

} catch (PDOException $e) {
    $cnx->rollBack();
    http_response_code(500);
    echo json_encode(['error' => 'Erreur serveur', 'details' => $e->getMessage()]);
}
