<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type");
header("Access-Control-Allow-Methods: POST");
header('Content-Type: application/json');

require_once(__DIR__ . '/../db/Database.php');
require_once(__DIR__ . '/../jwt/utils.php');

try {
    $data = json_decode(file_get_contents('php://input'), true);

    if (!$data) {
        http_response_code(400);
        echo json_encode(['error' => 'Pas de données reçues']);
        exit();
    }

    $email = trim($data['email'] ?? '');
    $password = trim($data['motDePasse'] ?? '');
    $prenom = trim($data['prenom'] ?? '');
    $nom = trim($data['nom'] ?? '');
    $telephone = trim($data['telephone'] ?? '');

    if ($email === '' || $password === '' || $prenom === '' || $nom === '') {
        http_response_code(400);
        echo json_encode(['error' => 'Champs requis manquants']);
        exit();
    }

    $cnx = Database::getInstance();

    // Vérifie si l'utilisateur existe 
    $stmt = $cnx->prepare("SELECT ID_UTILISATEUR FROM UTILISATEURS WHERE COURRIEL = :email");
    $stmt->bindParam(':email', $email);
    $stmt->execute();

    if ($stmt->fetch()) {
        http_response_code(409);
        echo json_encode(['error' => 'Email déjà utilisé']);
        exit();
    }

    $plainPassword = $password; // à remplacer par password_hash apres !!!
    $fullName = $prenom . ' ' . $nom;

    $stmt = $cnx->prepare("INSERT INTO UTILISATEURS (NOM, COURRIEL, MOT_DE_PASSE_HASH, TELEPHONE) 
                           VALUES (:nom, :email, :password, :telephone)");
    $stmt->bindParam(':nom', $fullName);
    $stmt->bindParam(':email', $email);
    $stmt->bindParam(':password', $plainPassword);
    $stmt->bindParam(':telephone', $telephone);
    $stmt->execute();

    $userId = $cnx->lastInsertId();

    // Génère un token
    $token = generate_jwt([
        'id' => $userId,
        'email' => $email,
        'nom' => $fullName,
        'exp' => time() + 3600
    ]);

    echo json_encode(['success' => true, 'token' => $token]);
    exit();

} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Erreur serveur',
        'details' => $e->getMessage()
    ]);
    exit();
}
