package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {
    private MySqlAuthDAO authDAO;
    private MySqlUserDAO userDAO;
    private String username;
    private UserData newUser;

    @BeforeEach
    void setup() throws ResponseException {
        userDAO = new MySqlUserDAO();
        userDAO.deleteAllUsers();

        newUser = new UserData("testUser", "password123", "test@mail.com");
        userDAO.insertUser(newUser);
        username = newUser.username();

        authDAO = new MySqlAuthDAO();
        authDAO.deleteAllAuthTokens();
    }

    @Test
    void insertAuthSuccess() throws ResponseException {
        AuthData authData = new AuthData("validToken123", username);

        assertDoesNotThrow(() -> authDAO.insertAuth(authData));

        AuthData retrievedAuth = authDAO.getAuth("validToken123");
        assertNotNull(retrievedAuth);
        assertEquals(username, retrievedAuth.username()); // Ensuring it maps correctly
    }

    @Test
    void getAuthSuccess() throws ResponseException {
        AuthData authData = new AuthData("retrievableToken", username);
        authDAO.insertAuth(authData);

        AuthData retrievedAuth = authDAO.getAuth("retrievableToken");

        assertNotNull(retrievedAuth);
        assertEquals(username, retrievedAuth.username());
    }

    @Test
    void getAuthFailNonExistentToken() throws ResponseException {
        AuthData retrievedAuth = authDAO.getAuth("nonExistentToken");

        assertNull(retrievedAuth); // Should return null since token doesn't exist
    }

    @Test
    void generateAuthTokenSuccess() {
        String token1 = authDAO.generateAuthToken();
        String token2 = authDAO.generateAuthToken();

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2); // Tokens should be unique
    }

    @Test
    void deleteAuthSuccess() throws ResponseException {
        AuthData authData = new AuthData("deleteToken", username);
        authDAO.insertAuth(authData);

        authDAO.deleteAuth("deleteToken");

        AuthData retrievedAuth = authDAO.getAuth("deleteToken");
        assertNull(retrievedAuth); // Should be deleted
    }

    @Test
    void deleteAuthFailNonExistentToken() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonExistentToken")); // Should not throw an error
    }

    @Test
    void deleteAllAuthTokensSuccess() throws ResponseException {
        authDAO.insertAuth(new AuthData("token1", username));
        authDAO.insertAuth(new AuthData("token2", username));

        authDAO.deleteAllAuthTokens();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }
}

