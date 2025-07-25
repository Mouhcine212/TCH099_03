<?php
require_once __DIR__.'/router.php'; 
$URL='/api';

header("Access-Control-Allow-Origin: *");

post($URL.'/login','endpoints/login.php');


post($URL.'/user','endpoints/user_post.php');

post('/api/user', 'endpoints/user_post.php');




