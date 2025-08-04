<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
header('Content-Type: application/json');

require_once(__DIR__ . '/../db/Database.php');

// Log debug pour confirmer que le script s'exécute
file_put_contents(__DIR__ . '/debug_annulation.txt', "SCRIPT START\n", FILE_APPEND);

// Lire le corps JSON envoyé par fetch
$rawInput = file_get_contents('php://input');
file_put_contents(__DIR__ . '/debug_annulation.txt', "RAW: $rawInput\n", FILE_APPEND);

$input = json_decode($rawInput, true);
file_put_contents(__DIR__ . '/debug_annulation.txt', "JSON: " . print_r($input, true) . "\n", FILE_APPEND);

// Vérifier la présence de l'ID de réservation
if (!isset($input['reservation_id'])) {
    echo json_encode([
        'success' => false,
        'message' => 'ID de réservation manquant'
    ]);
    file_put_contents(__DIR__ . '/debug_annulation.txt', "NO ID\n", FILE_APPEND);
    exit;
}

$reservation_id = intval($input['reservation_id']);

try {
    $cnx = Database::getInstance();
    file_put_contents(__DIR__ . '/debug_annulation.txt', "DB CONNECTED\n", FILE_APPEND);

    $stmt = $cnx->prepare("
        UPDATE RESERVATIONS
        SET STATUT = 'Annulée'
        WHERE ID_RESERVATION = :id
    ");
    $stmt->bindParam(':id', $reservation_id, PDO::PARAM_INT);

    if ($stmt->execute()) {
        echo json_encode([
            'success' => true,
            'message' => 'Réservation annulée avec succès',
            'reservation_id' => $reservation_id
        ]);
        file_put_contents(__DIR__ . '/debug_annulation.txt', "UPDATE SUCCESS\n", FILE_APPEND);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Échec de l\'annulation'
        ]);
        file_put_contents(__DIR__ . '/debug_annulation.txt', "UPDATE FAILED\n", FILE_APPEND);
    }
} catch (PDOException $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Erreur SQL: ' . $e->getMessage()
    ]);
    file_put_contents(__DIR__ . '/debug_annulation.txt', "PDO ERROR: ".$e->getMessage()."\n", FILE_APPEND);
}

exit;
