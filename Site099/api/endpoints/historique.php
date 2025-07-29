<?php
require_once(__DIR__ . '/../db/Database.php');
require_once(__DIR__ . '/../jwt/utils.php');

header('Content-Type: application/json');

// Récupération du token
$authHeader = '';
if (isset($_SERVER['HTTP_AUTHORIZATION'])) {
    $authHeader = $_SERVER['HTTP_AUTHORIZATION'];
} elseif (isset($_SERVER['REDIRECT_HTTP_AUTHORIZATION'])) {
    $authHeader = $_SERVER['REDIRECT_HTTP_AUTHORIZATION'];
} elseif (function_exists('getallheaders')) {
    $headers = getallheaders();
    if (isset($headers['Authorization'])) {
        $authHeader = $headers['Authorization'];
    }
}

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

try {
    $cnx = Database::getInstance();

    $stmt = $cnx->prepare("
        SELECT 
            R.*,
            V.ORIGINE,
            V.DESTINATION,
            V.HEURE_DEPART,
            V.HEURE_ARRIVEE,
            V.COMPAGNIE,
            V.PRIX,
            P.DATE_NAISSANCE,
            P.NUMERO_PASSEPORT
        FROM RESERVATIONS R
        JOIN VOLS V ON R.ID_VOL = V.ID_VOL
        LEFT JOIN PASSAGERS P ON R.ID_RESERVATION = P.ID_RESERVATION
        WHERE R.ID_UTILISATEUR = ?
        ORDER BY R.DATE_RESERVATION DESC
    ");
    $stmt->execute([$id_utilisateur]);

    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($result);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Erreur serveur',
        'details' => $e->getMessage()
    ]);
}
