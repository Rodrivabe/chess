package dataaccess;


import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


public class MySqlUserDAO implements UserDAO{

    public MySqlUserDAO() throws ResponseException {
        String[] createUserTableIfNotExist = {"""
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(50) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL,
                PRIMARY KEY (username)
            )
            """};
        DatabaseManager.configureDatabase(createUserTableIfNotExist);
    }


    public void insertUser(UserData user) throws ResponseException {
        var insertUserStatement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        DatabaseManager.executeUpdate(insertUserStatement, user.username(), hashedPassword, user.email());
    }

    public UserData getUser(String username) throws ResponseException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
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




    public void deleteAllUsers() throws ResponseException {

        var deleteAuthsStatement = "DELETE FROM auth";
        DatabaseManager.executeUpdate(deleteAuthsStatement);
        var deleteUsersStatement = "DELETE FROM users";
        DatabaseManager.executeUpdate(deleteUsersStatement);

    }
}
