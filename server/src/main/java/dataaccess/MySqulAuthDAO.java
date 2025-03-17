package dataaccess;

import exception.ResponseException;

public class MySqulAuthDAO extends DatabaseManager {

    public MySqulAuthDAO() throws ResponseException {
        String[] createUserTableIfNotExist = {"""
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(50) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
                FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
            )
            """};
        configureDataBase(createUserTableIfNotExist);
    }

}
