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
        configureDatabase();
    }

    private final String[] createUserTableIfNotExist = {"""
            CREATE TABLE IF NOT EXISTS users (
                userIDNum INT NOT NULL AUTO_INCREMENT,
                username VARCHAR(50) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL,
                PRIMARY KEY (userIDNum)
            )
            """};

    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createUserTableIfNotExist) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }



    public int insertUser(UserData user) throws ResponseException{
        var insertUserStatement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        return DatabaseManager.executeUpdate(insertUserStatement, user.username(), hashedPassword, user.email());
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
        var disableFKChecks = "SET FOREIGN_KEY_CHECKS=0;";
        DatabaseManager.executeUpdate(disableFKChecks);
        var deleteAuthsStatement = "DELETE FROM auth";
        DatabaseManager.executeUpdate(deleteAuthsStatement);
        var deleteUsersStatement = "DELETE FROM users";
        DatabaseManager.executeUpdate(deleteUsersStatement);
        var resetUsersAutoIncrement = "ALTER TABLE users AUTO_INCREMENT = 1;";
        DatabaseManager.executeUpdate(resetUsersAutoIncrement);
        var enableFKChecks = "SET FOREIGN_KEY_CHECKS=1;";
        DatabaseManager.executeUpdate(enableFKChecks);


    }
}
