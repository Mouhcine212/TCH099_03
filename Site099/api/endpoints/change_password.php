<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

require_once __DIR__ . '/../db/Database.php';
require_once __DIR__ . '/../jwt/utils.php';

$input = json_decode(file_get_contents('php://input'), true);
if (!$input || !isset($input['currentPassword']) || !isset($input['newPassword'])) {
    echo json_encode(['error' => 'Champs manquants']);
    exit;
}

// Vérification du token
$headers = getallheaders();
if (!isset($headers['Authorization']) || !preg_match('/Bearer\s(\S+)/', $headers['Authorization'], $matches)) {
    echo json_encode(['error' => 'Token manquant ou invalide']);
    exit;
}

$token = $matches[1];
$userData = verify_jwt($token);

if (!$userData || !isset($userData['id'])) {
    echo json_encode(['error' => 'Token invalide ou expiré']);
    exit;
}

$userId = intval($userData['id']);
$currentPassword = trim($input['currentPassword']);
$newPassword = trim($input['newPassword']);

try {
    $cnx = Database::getInstance();

    // Vérifier mot de passe actuel
    $stmt = $cnx->prepare("SELECT MOT_DE_PASSE_HASH FROM UTILISATEURS WHERE ID_UTILISATEUR = ?");
    $stmt->execute([$userId]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$user || $currentPassword !== $user['MOT_DE_PASSE_HASH']) {
        echo json_encode(['error' => 'Mot de passe actuel incorrect']);
        exit;
    }

    // Mettre à jour
    $stmt = $cnx->prepare("UPDATE UTILISATEURS SET MOT_DE_PASSE_HASH = ? WHERE ID_UTILISATEUR = ?");
    $stmt->execute([$newPassword, $userId]); // En clair, comme ton projet actuel

    echo json_encode(['success' => true, 'message' => 'Mot de passe changé avec succès']);

} catch (PDOException $e) {
    echo json_encode(['error' => 'Erreur SQL: ' . $e->getMessage()]);
}
