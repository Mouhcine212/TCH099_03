<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Authorization, Content-Type');

require_once __DIR__ . '/../db/config.php';
require_once __DIR__ . '/../jwt/utils.php';

// Vérifier le token
$headers = getallheaders();
if (!isset($headers['Authorization'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Token manquant']);
    exit;
}

$jwt = str_replace('Bearer ', '', $headers['Authorization']);
$decoded = decodeJWT($jwt);

if (!$decoded || (!isset($decoded['id']) && !isset($decoded['user_id']))) {
    http_response_code(403);
    echo json_encode(['error' => 'Token invalide']);
    exit;
}

$user_id = $decoded['user_id'] ?? $decoded['id'];

// Récupérer le JSON de la requête
$data = json_decode(file_get_contents('php://input'), true);
$flight_id = $data['id_vol'] ?? null;
$seat_number = $data['numero_siege'] ?? null;

if (!$flight_id || !$seat_number) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit;
}

// --- Connexion MySQL avec SSL pour Azure ---
$conn = mysqli_init();

// Désactiver la vérification stricte du certificat SSL (Azure Student)
mysqli_ssl_set($conn, NULL, NULL, NULL, NULL, NULL);

if (!mysqli_real_connect(
    $conn,
    DB_HOST,       // Exemple : flightets-sql.mysql.database.azure.com
    DB_USER,       // Exemple : adminflight@flightets-sql
    DB_PASS,
    DB_NAME,
    3306,
    NULL,
    MYSQLI_CLIENT_SSL_DONT_VERIFY_SERVER_CERT
)) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur connexion DB SSL: ' . mysqli_connect_error()]);
    exit;
}

try {
    $stmt = $conn->prepare("
        INSERT INTO RESERVATIONS (ID_UTILISATEUR, ID_VOL, NUMERO_SIEGE, STATUT)
        VALUES (?, ?, ?, 'Confirmée')
    ");
    $stmt->bind_param('iis', $user_id, $flight_id, $seat_number);
    $stmt->execute();

    echo json_encode([
        'success' => true,
        'id_reservation' => $conn->insert_id
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur serveur: ' . $e->getMessage()]);
}

$conn->close();
