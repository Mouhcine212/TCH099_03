<?php
require_once __DIR__ . '/router.php'; 
$URL = '/api';

header("Access-Control-Allow-Origin: *");

// === Routes POST ===
post($URL . '/login', 'endpoints/login.php');
post($URL . '/user', 'endpoints/user_post.php');
post($URL . '/search_flights', 'endpoints/search_flights.php');
post($URL . '/reserve', 'endpoints/reserve_post.php');


// === Route GET sans fonction ===
get($URL . '/get_flight_by_id/$id', 'endpoints/get_flight_by_id.php');
