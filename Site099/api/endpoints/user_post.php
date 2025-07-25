<?php
require_once(__DIR__ . '/../db/Database.php');

header('Content-Type: application/json');

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['email'], $data['password'], $data['firstName'], $data['lastName'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit();
}

$email = trim($data['email']);
$password = $data['password'];
$firstName = trim($data['firstName']);
$lastName = trim($data['lastName']);

if ($email === '' || $password === '' || $firstName === '' || $lastName === '') {
    http_response_code(400);
    echo json_encode(['error' => 'Tous les champs sont requis']);
    exit();
}

try {
    $cnx = Database::getInstance();

    // Vérifie si l’email existe déjà
    $check = $cnx->prepare("SELECT id FROM users WHERE email = :email");
    $check->bindParam(':email', $email);
    $check->execute();

    if ($check->fetch()) {
        http_response_code(409);
        echo json_encode(['error' => 'Cet email est déjà utilisé']);
        exit();
    }

    $stmt = $cnx->prepare("INSERT INTO users (email, password, first_name, last_name, role, created_at, updated_at)
                           VALUES (:email, :password, :first_name, :last_name, 'passenger', NOW(), NOW())");

    $stmt->bindParam(':email', $email);
    $stmt->bindParam(':password', $password); 
    $stmt->bindParam(':first_name', $firstName);
    $stmt->bindParam(':last_name', $lastName);

    $stmt->execute();

    echo json_encode(['success' => true]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Erreur serveur', 'details' => $e->getMessage()]);
}
