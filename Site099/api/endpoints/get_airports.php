<?php
require_once(__DIR__ . '/../db/Database.php');

header('Content-Type: application/json');

$headers = getallheaders();
if (!isset($headers['Authorization'])) {
    http_response_code(401);
    echo json_encode(['error' => 'Token manquant']);
    exit();
}

$token = str_replace('Bearer ', '', $headers['Authorization']);

try {
    $cnx = Database::getInstance();
    $pstmt = $cnx->prepare("SELECT VILLE, CODE_IATA FROM AEROPORTS ORDER BY VILLE ASC");
    $pstmt->execute();
    $results = $pstmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($results);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => $e->getMessage()]);
}
