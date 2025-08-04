<?php
require_once __DIR__ . '/router.php'; 
$URL = '/api';

header("Access-Control-Allow-Origin: *");

post($URL . '/login', 'endpoints/login.php');

post($URL . '/user', 'endpoints/user_post.php');

post($URL . '/search_flights', 'endpoints/search_flights.php');

get($URL . '/get_flight_by_id/$id', 'endpoints/get_flight_by_id.php');

post($URL . '/paiement', 'endpoints/paiement.php');

get($URL . '/historique', 'endpoints/historique.php');

post($URL . '/annuler_reservation', 'endpoints/annuler_reservation.php');

post($URL . '/update_user', 'endpoints/update_user.php');

post($URL . '/change_password', 'endpoints/change_password.php');

post($URL . '/create_reservation', 'endpoints/create_reservation.php');

post($URL . '/process_payment', 'endpoints/process_payment.php');

get('/api/get_airports', 'endpoints/get_airports.php');

get($URL . '/get_user', 'endpoints/get_user.php');

post($URL . '/create_reservation', 'endpoints/create_reservation.php');

