<?php
require_once(__DIR__ . '/config.php');

class Database
{
    private static $instance = null;

    private function __construct() {}

    public static function getInstance()
    {
        if (self::$instance === null) {
            try {
                // Azure MySQL Flexible Server nécessite SSL
                $options = [
                    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::MYSQL_ATTR_SSL_CA       => '/etc/ssl/certs/ca-certificates.crt', // Certificat global Linux
                    PDO::MYSQL_ATTR_SSL_VERIFY_SERVER_CERT => false // Azure ne l'exige pas
                ];

                // Connexion PDO
                self::$instance = new PDO(
                    "mysql:host=" . Config::DB_HOST . ";dbname=" . Config::DB_NAME . ";charset=utf8",
                    Config::DB_USER,
                    Config::DB_PWD,
                    $options
                );

                // Log succès
                file_put_contents(
                    __DIR__ . '/../log_inscription.txt',
                    date('Y-m-d H:i:s') . " ✅ Connexion MySQL réussie\n",
                    FILE_APPEND
                );

            } catch (PDOException $e) {
                // Log de l'erreur
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
