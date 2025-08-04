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

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['origine']) || !isset($data['destination'])) {
    http_response_code(400);
    echo json_encode(['error' => 'Champs manquants']);
    exit();
}

$origineInput = trim($data['origine']);
$destinationInput = trim($data['destination']);
$dateDepart = isset($data['dateDepart']) ? trim($data['dateDepart']) : '';


function cleanAirportInput($str) {
    if (preg_match('/^(.+)\s+\((\w{3})\)$/', $str, $matches)) {
        return [
            'ville' => trim($matches[1]),
            'code' => strtoupper($matches[2])
        ];
    }
    return ['ville' => $str, 'code' => $str];
}

$origineData = cleanAirportInput($origineInput);
$destinationData = cleanAirportInput($destinationInput);

try {
    $cnx = Database::getInstance();

    $sql = "
        SELECT V.ID_VOL,
               ao.VILLE AS ORIGINE_VILLE, ao.CODE_IATA AS ORIGINE_CODE,
               ad.VILLE AS DESTINATION_VILLE, ad.CODE_IATA AS DESTINATION_CODE,
               V.HEURE_DEPART, V.HEURE_ARRIVEE,
               V.COMPAGNIE, V.NUMERO_VOL, V.PRIX, V.CLASSE, V.SIEGES_DISPONIBLES
        FROM VOLS V
        JOIN AEROPORTS ao ON ao.ID_AEROPORT = V.ID_AEROPORT_ORIGINE
        JOIN AEROPORTS ad ON ad.ID_AEROPORT = V.ID_AEROPORT_DESTINATION
        WHERE (ao.VILLE LIKE :origVille OR ao.CODE_IATA LIKE :origCode)
          AND (ad.VILLE LIKE :destVille OR ad.CODE_IATA LIKE :destCode)
    ";

    if ($dateDepart !== '') {
        $sql .= " AND DATE(V.HEURE_DEPART) = :dateDepart";
    }

    $sql .= " ORDER BY V.HEURE_DEPART ASC";

    $pstmt = $cnx->prepare($sql);
    $pstmt->bindValue(':origVille', "%{$origineData['ville']}%");
    $pstmt->bindValue(':origCode', "%{$origineData['code']}%");
    $pstmt->bindValue(':destVille', "%{$destinationData['ville']}%");
    $pstmt->bindValue(':destCode', "%{$destinationData['code']}%");
    if ($dateDepart !== '') {
        $pstmt->bindValue(':dateDepart', $dateDepart);
    }

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
