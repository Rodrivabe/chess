package dataaccess;


import exception.ResponseException;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


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



    public void insertUser(UserData user) throws ResponseException{
        var insertUserStatement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(insertUserStatement, user.username(), user.password(), user.email());
    }

    public UserData getUser(String username) {
        return null;
    }

    public Collection<UserData> listUsers() throws ResponseException{
        Collection<UserData> result = new ArrayList<>();
        var listUsersStatement = "SELECT username, password, email FROM users";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(listUsersStatement);
             var rs = ps.executeQuery()){
            while (rs.next()) {
                result.add(readUser(rs));
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;

    }

    private UserData readUser(ResultSet rs) throws SQLException {
        return new UserData(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email")
        );
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
