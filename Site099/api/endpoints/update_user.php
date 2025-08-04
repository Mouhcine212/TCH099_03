<?php
require_once __DIR__ . '/../db/Database.php';
require_once __DIR__ . '/../jwt/utils.php';

header('Content-Type: application/json');

$headers = getallheaders();
if (!isset($headers['Authorization'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Token manquant']);
    exit();
}

$token = str_replace('Bearer ', '', $headers['Authorization']);
$userData = verify_jwt($token);

if (!$userData) {
    http_response_code(401);
    echo json_encode(['error' => 'Token invalide ou expiré']);
    exit();
}

$data = json_decode(file_get_contents('php://input'), true);

$nom = trim($data['nom'] ?? '');
$email = trim($data['email'] ?? '');
$telephone = trim($data['telephone'] ?? '');

if ($nom === '' || $email === '') {
    http_response_code(400);
    echo json_encode(['error' => 'Nom et email requis']);
    exit();
}

try {
    $db = Database::getInstance();
    $stmt = $db->prepare("
        UPDATE UTILISATEURS
        SET NOM = :nom,
            COURRIEL = :email,
            TELEPHONE = :telephone
        WHERE ID_UTILISATEUR = :id
    ");

    $stmt->execute([
        ':nom' => $nom,
        ':email' => $email,
        ':telephone' => $telephone,
        ':id' => $userData['id']
    ]);

    $newToken = generate_jwt([
        'id' => $userData['id'],
        'nom' => $nom,
        'email' => $email,
        'telephone' => $telephone,
        'exp' => time() + 3600 
    ]);

    echo json_encode([
        'success' => true,
        'message' => 'Profil mis à jour avec succès',
        'token' => $newToken
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur SQL: ' . $e->getMessage()]);
}

