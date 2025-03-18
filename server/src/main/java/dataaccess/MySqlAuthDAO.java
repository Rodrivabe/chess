package dataaccess;

import exception.ResponseException;
import model.AuthData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() throws ResponseException {
        String[] createUserTableIfNotExist = {"""
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(50) NOT NULL,
                PRIMARY KEY (authToken),
                FOREIGN KEY (username) REFERENCES users(username)
            )
            """};
        DatabaseManager.configureDataBase(createUserTableIfNotExist);
    }

    public void insertAuth(AuthData authdata) throws ResponseException {
        var insertUserStatement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
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

    public String generateAuthToken() throws ResponseException {
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
