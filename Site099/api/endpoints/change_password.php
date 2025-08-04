<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Access-Control-Allow-Methods: POST');

require_once(__DIR__ . '/../db/Database.php');
require_once(__DIR__ . '/../jwt/utils.php');

try {
    $headers = getallheaders();
    $authHeader = $headers['Authorization'] ?? '';

    if (!$authHeader || !str_starts_with($authHeader, 'Bearer ')) {
        http_response_code(401);
        echo json_encode(['error' => 'Token manquant']);
        exit();
    }

    $token = substr($authHeader, 7);
    $userData = verify_jwt($token);

    if (!$userData || !isset($userData['id'])) {
        http_response_code(401);
        echo json_encode(['error' => 'Token invalide']);
        exit();
    }

    $userId = $userData['id'];
    $data = json_decode(file_get_contents('php://input'), true);

    $currentPassword = trim($data['currentPassword'] ?? '');
    $newPassword = trim($data['newPassword'] ?? '');

    if ($currentPassword === '' || $newPassword === '') {
        http_response_code(400);
        echo json_encode(['error' => 'Champs manquants']);
        exit();
    }

    if (strlen($newPassword) < 6) {
        http_response_code(400);
        echo json_encode(['error' => 'Le mot de passe doit contenir au moins 6 caractères']);
        exit();
    }

    $cnx = Database::getInstance();
    $stmt = $cnx->prepare("SELECT MOT_DE_PASSE_HASH FROM UTILISATEURS WHERE ID_UTILISATEUR = :id");
    $stmt->execute([':id' => $userId]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$user || !password_verify($currentPassword, $user['MOT_DE_PASSE_HASH'])) {
        http_response_code(400);
        echo json_encode(['error' => 'Mot de passe actuel incorrect']);
        exit();
    }

    $hashedPassword = password_hash($newPassword, PASSWORD_BCRYPT);

    $update = $cnx->prepare("UPDATE UTILISATEURS SET MOT_DE_PASSE_HASH = :pwd WHERE ID_UTILISATEUR = :id");
    $update->execute([
        ':pwd' => $hashedPassword,
        ':id' => $userId
    ]);

    echo json_encode(['success' => true, 'message' => 'Mot de passe mis à jour avec succès']);

} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Erreur serveur',
        'details' => $e->getMessage()
    ]);
}
