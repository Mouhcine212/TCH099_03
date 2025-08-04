<?php
interface Config
{   
    const DB_HOST = "database:3306";
    const DB_USER = "root";
    const DB_PWD = "tiger";
    const DB_NAME = "docker";
}

define('DB_HOST', Config::DB_HOST);
define('DB_USER', Config::DB_USER);
define('DB_PASS', Config::DB_PWD);
define('DB_NAME', Config::DB_NAME);
