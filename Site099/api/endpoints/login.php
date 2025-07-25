<?php
require_once('./jwt/utils.php');
require_once('./db/Database.php');

header('Content-Type: application/json');

$data = json_decode(file_get_contents('php://input'), true);

// Vérifie les champs
if (!isset($data['email']) || !isset($data['motDePasse'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit();
}

$username = trim($data['email']);
$mdp = $data['motDePasse'];

if ($username === '' || $mdp === '') {
    http_response_code(401);
    echo json_encode(['error' => 'Identifiants invalides']);
    exit();
}

try {
    $cnx = Database::getInstance();
    $pstmt = $cnx->prepare("SELECT * FROM users WHERE email = :email");
    $pstmt->bindParam(':email', $username);
    $pstmt->execute();

    $user = $pstmt->fetch(PDO::FETCH_ASSOC);

    if ($user && $mdp === $user['password']) {
        $token = generate_jwt([
            'id' => $user['id'],
            'email' => $user['email'],
            'role' => $user['role'],
            'exp' => time() + 3600 // expire en 1h
        ]);
        echo json_encode(['token' => $token]);
        exit();
    }

    http_response_code(401);
    echo json_encode(['error' => 'Identifiants invalides']);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Erreur serveur',
        'message' => $e->getMessage()
    ]);
}
