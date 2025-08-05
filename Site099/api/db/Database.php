<?php
require_once(__DIR__ . '/config.php');

class Database
{
    private static $instance = null;

    private function __construct() {}

    public static function getInstance(): PDO
    {
        if (self::$instance === null) {
            try {
                // ✅ Options PDO pour Azure MySQL Flexible Server avec SSL
                $options = [
                    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_EMULATE_PREPARES   => false,
                    PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8mb4",
                    
                    // 🔹 SSL obligatoire pour Azure
                    PDO::MYSQL_ATTR_SSL_CA       => __DIR__ . '/DigiCertGlobalRootCA.crt.pem',
                    PDO::MYSQL_ATTR_SSL_VERIFY_SERVER_CERT => false
                ];

                // ✅ Connexion PDO avec SSL
                self::$instance = new PDO(
                    "mysql:host=" . Config::DB_HOST . ";dbname=" . Config::DB_NAME . ";charset=utf8mb4",
                    Config::DB_USER,
                    Config::DB_PWD,
                    $options
                );

                // ✅ Log succès
                file_put_contents(
                    __DIR__ . '/../log_inscription.txt',
                    date('Y-m-d H:i:s') . " ✅ Connexion MySQL réussie via SSL\n",
                    FILE_APPEND
                );

            } catch (PDOException $e) {
                // ❌ Log de l'erreur
                file_put_contents(
                    __DIR__ . '/../log_inscription.txt',
                    date('Y-m-d H:i:s') . " ❌ Erreur MySQL : " . $e->getMessage() . "\n",
                    FILE_APPEND
                );
                throw $e;
            }
        }

        return self::$instance;
    }
}
