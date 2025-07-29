<?php
require_once(__DIR__ . '/../db/Database.php');

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['destination']) || !isset($data['date'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit();
}

$destination = $data['destination'];
$date = $data['date'];

try {
    $cnx = Database::getInstance();
    $pstmt = $cnx->prepare("
        SELECT ID_VOL, ORIGINE, DESTINATION, HEURE_DEPART, HEURE_ARRIVEE, COMPAGNIE, NUMERO_VOL, PRIX, CLASSE, SIEGES_DISPONIBLES
        FROM VOLS
        WHERE DESTINATION LIKE :destination
        AND DATE(HEURE_DEPART) = :date
    ");

    $likeDest = "%" . $destination . "%";
    $pstmt->bindParam(':destination', $likeDest);
    $pstmt->bindParam(':date', $date);
    $pstmt->execute();

    $results = $pstmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($results);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'error' => 'Erreur serveur',
        'details' => $e->getMessage()
    ]);
}
