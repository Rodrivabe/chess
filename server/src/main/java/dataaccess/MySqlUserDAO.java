package dataaccess;


import exception.ResponseException;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySqlUserDAO extends DatabaseManager implements UserDAO{

    public MySqlUserDAO() throws ResponseException {
        String[] createUserTableIfNotExist = {"""
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(50) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL
                PRIMARY KEY (username)
            )
            """};
        configureDataBase(createUserTableIfNotExist);
    }



    public void insertUser(UserData user) throws  R{
        var insertUserStatement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(insertUserStatement, user.username(), user.password(), user.email());
    }

    public UserData getUser(String username) {
        return null;
    }

    public Collection<UserData> listUsers() throws ResponseException{

        return List.of();
    }

    public void deleteUser(String username) throws ResponseException {
        var deleteAuthStatement = "DELETE FROM auth WHERE username=?";
        executeUpdate(deleteAuthStatement, username);
        var deleteUserStatement = "DELETE FROM user WHERE username=?";
        executeUpdate(deleteUserStatement, username);
    }

    public void deleteAllUsers() throws ResponseException {
        var deleteUsersStatement = "DELETE FROM auth WHERE username=?";
        executeUpdate(deleteUsersStatement);
    }
}
