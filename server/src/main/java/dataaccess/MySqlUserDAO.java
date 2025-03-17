package dataaccess;


import exception.ResponseException;

import java.sql.SQLException;

public class MySqlUserDAO extends DatabaseManager{

    public MySqlUserDAO() throws ResponseException {
        String[] createUserTableIfNotExist = {"""
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(50) NOY NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL
                PRIMARY KEY (username)
            )
            CREATE TABLE IF NOT EXISTS  pet (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """};
        configureDataBase(createUserTableIfNotExist);
    }


}
