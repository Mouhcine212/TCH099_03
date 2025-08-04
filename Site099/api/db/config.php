<?php
interface Config
{
    const DB_HOST = "flightets-sql.mysql.database.azure.com:3306"; // Serveur Azure
    const DB_USER = "adminflight";                                // Utilisateur Azure
    const DB_PWD  = "Mouchcine123";                               // Mot de passe Azure
    const DB_NAME = "new_booker_updated";                                  // Nom de ta base
}

define('DB_HOST', Config::DB_HOST);
define('DB_USER', Config::DB_USER);
define('DB_PASS', Config::DB_PWD);
define('DB_NAME', Config::DB_NAME);
?>
