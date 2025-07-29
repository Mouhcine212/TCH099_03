<?php
require_once __DIR__ . '/router.php'; 
$URL = '/api';

header("Access-Control-Allow-Origin: *");

post($URL . '/login', 'endpoints/login.php');
post($URL . '/user', 'endpoints/user_post.php');
post($URL . '/search_flights', 'endpoints/search_flights.php');
post($URL . '/reserve', 'endpoints/reserve_post.php');

get($URL . '/get_flight_by_id/$id', 'endpoints/get_flight_by_id.php');

post($URL . '/paiement', 'endpoints/paiement.php');

get($URL . '/historique', 'endpoints/historique.php');
