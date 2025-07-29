<?php

file_put_contents("headers_debug.txt", print_r(getallheaders(), true));

require_once(__DIR__ . '/../db/Database.php');
require_once(__DIR__ . '/../jwt/utils.php');

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Méthode non autorisée']);
    exit;
}

$authHeader = $_SERVER['HTTP_AUTHORIZATION']
    ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION']
    ?? (getallheaders()['Authorization'] ?? '');

if (!preg_match('/Bearer\s(\S+)/', $authHeader, $matches)) {
    http_response_code(401);
    echo json_encode(['error' => 'Token manquant']);
    exit;
}

$token = $matches[1];
$decoded = verify_jwt($token);
if (!$decoded || !is_array($decoded)) {
    http_response_code(401);
    echo json_encode(['error' => 'Token invalide']);
    exit;
}

$id_utilisateur = $decoded['id'];
$nom_utilisateur = $decoded['nom'] ?? 'Passager inconnu';

$data = json_decode(file_get_contents('php://input'), true);
$id_vol = $data['id_vol'] ?? null;
$methode = $data['methode'] ?? null;
$numero_siege = $data['numero_siege'] ?? null;
$dateNaissance = trim($data['date_naissance'] ?? '');
$numeroPasseport = trim($data['numero_passeport'] ?? '');

if (!$id_vol || !$methode || !$numero_siege) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit;
}

try {
    $cnx = Database::getInstance();
    $cnx->beginTransaction();

    // 1. Réservation
    $stmt = $cnx->prepare("INSERT INTO RESERVATIONS (ID_UTILISATEUR, ID_VOL, NUMERO_SIEGE, STATUT) VALUES (?, ?, ?, 'Confirmée')");
    $stmt->execute([$id_utilisateur, $id_vol, $numero_siege]);
    $reservationId = $cnx->lastInsertId();

    // 2. Passager avec données
    $stmt = $cnx->prepare("INSERT INTO PASSAGERS (ID_RESERVATION, NOM_COMPLET, DATE_NAISSANCE, NUMERO_PASSEPORT)
                           VALUES (?, ?, ?, ?)");
    $stmt->execute([$reservationId, $nom_utilisateur, $dateNaissance ?: null, $numeroPasseport ?: null]);

    // 3. Prix du vol
    $stmt = $cnx->prepare("SELECT PRIX FROM VOLS WHERE ID_VOL = ?");
    $stmt->execute([$id_vol]);
    $prix = $stmt->fetchColumn();

    // 4. Paiement
    $stmt = $cnx->prepare("INSERT INTO PAIEMENTS (ID_RESERVATION, DATE_PAIEMENT, MONTANT, METHODE, STATUT)
                           VALUES (?, NOW(), ?, ?, 'Payé')");
    $stmt->execute([$reservationId, $prix, $methode]);

    // 5. Mise à jour des sièges
    $cnx->prepare("UPDATE VOLS SET SIEGES_DISPONIBLES = SIEGES_DISPONIBLES - 1 WHERE ID_VOL = ?")
        ->execute([$id_vol]);

    $cnx->commit();

    echo json_encode([
        'message' => 'Paiement confirmé',
        'siege_attribue' => $numero_siege
    ]);
} catch (Exception $e) {
    if ($cnx->inTransaction()) $cnx->rollBack();
    http_response_code(500);
    echo json_encode([
        'error' => 'Erreur serveur',
        'details' => $e->getMessage()
    ]);
}
