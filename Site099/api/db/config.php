<?php
interface Config
{
    const DB_HOST = "flightets-sql.mysql.database.azure.com"; 
    const DB_USER = "adminflight";          // ✅ sans @flightets-sql
    const DB_PWD  = "Mouhcine123";          
    const DB_NAME = "flightets";            
}

define('DB_HOST', Config::DB_HOST);
define('DB_USER', Config::DB_USER);
define('DB_PASS', Config::DB_PWD);
define('DB_NAME', Config::DB_NAME);
?>
