<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type");
header("Access-Control-Allow-Methods: POST");
header('Content-Type: application/json');

$rawInput = file_get_contents('php://input');
$logFile = __DIR__ . '/log_inscription.txt';

// --- Log début script ---
file_put_contents($logFile, PHP_EOL . "===== ".date('Y-m-d H:i:s')." : Début user_post.php =====" . PHP_EOL, FILE_APPEND);
file_put_contents($logFile, "Requête brute: " . $rawInput . PHP_EOL, FILE_APPEND);
error_log("🔹 [user_post.php] Début traitement - Données brutes : $rawInput");

// Charger dépendances
require_once(__DIR__ . '/db/Database.php');
require_once(__DIR__ . '/jwt/utils.php');

try {
    $data = json_decode($rawInput, true);

    if (!$data) {
        http_response_code(400);
        echo json_encode(['error' => 'Pas de données reçues']);
        file_put_contents($logFile, "❌ JSON vide ou invalide" . PHP_EOL, FILE_APPEND);
        error_log("❌ [user_post.php] JSON vide ou invalide");
        exit();
    }

    $email = trim($data['email'] ?? '');
    $password = trim($data['motDePasse'] ?? '');
    $prenom = trim($data['prenom'] ?? '');
    $nom = trim($data['nom'] ?? '');
    $telephone = trim($data['telephone'] ?? '');

    // Log des champs reçus
    file_put_contents($logFile, "Champs reçus: email=$email | prenom=$prenom | nom=$nom | tel=$telephone".PHP_EOL, FILE_APPEND);
    error_log("🔹 Champs reçus: email=$email, prenom=$prenom, nom=$nom, tel=$telephone");

    if ($email === '' || $password === '' || $prenom === '' || $nom === '') {
        http_response_code(400);
        echo json_encode(['error' => 'Champs requis manquants']);
        file_put_contents($logFile, "❌ Champs requis manquants" . PHP_EOL, FILE_APPEND);
        error_log("❌ [user_post.php] Champs requis manquants");
        exit();
    }

    // Test de connexion SQL
    file_put_contents($logFile, "Tentative de connexion SQL..." . PHP_EOL, FILE_APPEND);
    error_log("🔹 Tentative de connexion SQL...");
    $cnx = Database::getInstance();
    file_put_contents($logFile, "✅ Connexion SQL réussie" . PHP_EOL, FILE_APPEND);
    error_log("✅ Connexion SQL réussie");

    // Vérification email existant
    $stmt = $cnx->prepare("SELECT ID_UTILISATEUR FROM UTILISATEURS WHERE COURRIEL = :email");
    $stmt->bindParam(':email', $email);
    $stmt->execute();

    if ($stmt->fetch()) {
        http_response_code(409);
        echo json_encode(['error' => 'Email déjà utilisé']);
        file_put_contents($logFile, "⚠ Email déjà utilisé: $email" . PHP_EOL, FILE_APPEND);
        error_log("⚠ Email déjà utilisé: $email");
        exit();
    }

    // Hash mot de passe et insertion
    $hashedPassword = password_hash($password, PASSWORD_BCRYPT);
    $fullName = $prenom . ' ' . $nom;

    $stmt = $cnx->prepare("
        INSERT INTO UTILISATEURS (NOM, COURRIEL, MOT_DE_PASSE_HASH, TELEPHONE) 
        VALUES (:nom, :email, :password, :telephone)
    ");
    $stmt->bindParam(':nom', $fullName);
    $stmt->bindParam(':email', $email);
    $stmt->bindParam(':password', $hashedPassword);
    $stmt->bindParam(':telephone', $telephone);
    $stmt->execute();

    $userId = $cnx->lastInsertId();
    file_put_contents($logFile, "✅ Nouvel utilisateur ID=$userId inséré." . PHP_EOL, FILE_APPEND);
    error_log("✅ Nouvel utilisateur ID=$userId inséré");

    // Génération token
    $token = generate_jwt([
        'id' => $userId,
        'email' => $email,
        'nom' => $fullName,
        'telephone' => $telephone,
        'exp' => time() + 3600
    ]);

    echo json_encode(['success' => true, 'token' => $token]);
    file_put_contents($logFile, "✅ Succès renvoyé au client" . PHP_EOL, FILE_APPEND);
    error_log("✅ [user_post.php] Succès renvoyé");

    exit();

} catch (Throwable $e) {
    http_response_code(500);
    $errorMsg = 'Erreur serveur : '.$e->getMessage();
    echo json_encode([
        'error' => 'Erreur serveur',
        'details' => $e->getMessage()
    ]);

    file_put_contents($logFile, "❌ Exception: " . $e->getMessage() . PHP_EOL, FILE_APPEND);
    error_log("❌ [user_post.php] Exception : " . $e->getMessage());
    exit();
}

