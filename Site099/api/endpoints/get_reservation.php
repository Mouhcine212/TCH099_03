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

$request_uri = explode('/', trim($_SERVER['REQUEST_URI'], '/'));
$idReservation = end($request_uri);

if (!$idReservation || !is_numeric($idReservation)) {
    http_response_code(400);
    echo json_encode(['error' => 'ID réservation manquant ou invalide']);
    exit();
}

try {
    $cnx = Database::getInstance();

    $sql = "
        SELECT 
            r.ID_RESERVATION,
            r.NUMERO_SIEGE,
            r.STATUT AS STATUT_RESERVATION,
            r.DATE_RESERVATION,

            v.ID_VOL,
            v.COMPAGNIE,
            v.NUMERO_VOL,
            v.HEURE_DEPART,
            v.HEURE_ARRIVEE,
            v.CLASSE,
            v.PRIX,

            ao.VILLE AS ORIGINE,
            ao.CODE_IATA AS ORIGINE_IATA,
            ad.VILLE AS DESTINATION,
            ad.CODE_IATA AS DESTINATION_IATA

        FROM RESERVATIONS r
        INNER JOIN VOLS v ON r.ID_VOL = v.ID_VOL
        INNER JOIN AEROPORTS ao ON v.ID_AEROPORT_ORIGINE = ao.ID_AEROPORT
        INNER JOIN AEROPORTS ad ON v.ID_AEROPORT_DESTINATION = ad.ID_AEROPORT
        WHERE r.ID_RESERVATION = ? AND r.ID_UTILISATEUR = ?
        LIMIT 1
    ";

    $pstmt = $cnx->prepare($sql);
    $pstmt->execute([$idReservation, $idUtilisateur]);
    $reservation = $pstmt->fetch(PDO::FETCH_ASSOC);

    if (!$reservation) {
        http_response_code(404);
        echo json_encode(['error' => 'Réservation introuvable']);
        exit();
    }

    echo json_encode($reservation);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur serveur', 'details' => $e->getMessage()]);
}
