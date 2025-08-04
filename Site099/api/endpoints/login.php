<?php
require_once('./jwt/utils.php');
require_once('./db/Database.php');

header('Content-Type: application/json');

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['email']) || !isset($data['motDePasse'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit();
}

$email = trim($data['email']);
$password = trim($data['motDePasse']);

if ($email === '' || $password === '') {
    http_response_code(401);
    echo json_encode(['error' => 'Champs vides']);
    exit();
}

try {
    $cnx = Database::getInstance();
    $pstmt = $cnx->prepare("SELECT * FROM UTILISATEURS WHERE COURRIEL = :email");
    $pstmt->bindParam(':email', $email);
    $pstmt->execute();

    $user = $pstmt->fetch(PDO::FETCH_ASSOC);

    if (!$user) {
        http_response_code(401);
        echo json_encode(['error' => 'Utilisateur non trouvé']);
        exit();
    }

    // Vérification du mot de passe (en clair pour ton projet actuel)
    if ($password !== $user['MOT_DE_PASSE_HASH']) {
        http_response_code(401);
        echo json_encode(['error' => 'Mot de passe incorrect']);
        exit();
    }

    // 🔹 Créer un JWT complet avec toutes les infos utiles
    $token = generate_jwt([
        'id' => $user['ID_UTILISATEUR'],
        'nom' => $user['NOM'],
        'email' => $user['COURRIEL'],
        'telephone' => $user['TELEPHONE'],
        'exp' => time() + 3600
    ]);

    echo json_encode([
        'token' => $token,
        'user' => [
            'id' => $user['ID_UTILISATEUR'],
            'nom' => $user['NOM'],
            'email' => $user['COURRIEL'],
            'telephone' => $user['TELEPHONE']
        ]
    ]);
    exit();
    
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Erreur serveur',
        'message' => $e->getMessage()
    ]);
}
