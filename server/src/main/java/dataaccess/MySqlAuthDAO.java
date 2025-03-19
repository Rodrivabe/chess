package dataaccess;

import exception.ResponseException;
import model.AuthData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() throws ResponseException {
        configureDatabase();
    }

    private final String[] createUserTableIfNotExist = {"""
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL,
                userID INT NOT NULL,
                PRIMARY KEY (authToken),
                FOREIGN KEY (userID) REFERENCES users(userIDNum) ON DELETE CASCADE
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


    public void insertAuth(AuthData authdata) throws ResponseException {
        var insertUserStatement = "INSERT INTO auth (authToken, userID) VALUES (?, (SELECT userIDNum FROM users " +
                "WHERE username = ?))";
        DatabaseManager.executeUpdate(insertUserStatement, authdata.authToken(), authdata.username());
    }

    public AuthData getAuth(String authToken) throws ResponseException {

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        return new AuthData(
                rs.getString("authToken"),
                rs.getString("username")
        );
    }

    public String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public void deleteAuth(String authToken) throws ResponseException {
        var deleteAuthStatement = "DELETE FROM auth WHERE authToken=?";
        DatabaseManager.executeUpdate(deleteAuthStatement, authToken);
    }

    public void deleteAllAuthTokens() throws ResponseException {
        var deleteAuthsStatement = "TRUNCATE TABLE auth";
        DatabaseManager.executeUpdate(deleteAuthsStatement);
    }



}
