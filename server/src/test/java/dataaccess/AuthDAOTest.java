package dataaccess;


import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {
    private MySqlAuthDAO authDAO;

    @BeforeEach
    void setup() throws ResponseException {

        MySqlUserDAO userDAO = new MySqlUserDAO();
        userDAO.deleteAllUsers();
        userDAO.insertUser(new UserData("testUser", "password123", "test@mail.com"));

        authDAO = new MySqlAuthDAO();
        authDAO.deleteAllAuthTokens(); // Clear auth tokens before each test
    }

    @Test
    void insertAuth_Success() throws ResponseException {
        AuthData authData = new AuthData("validToken123", "testUser");

        assertDoesNotThrow(() -> authDAO.insertAuth(authData));

        AuthData retrievedAuth = authDAO.getAuth("validToken123");
        assertNotNull(retrievedAuth);
        assertEquals("testUser", retrievedAuth.username());
    }


    @Test
    void getAuth_Success() throws ResponseException {
        AuthData authData = new AuthData("retrievableToken", "testUser");
        authDAO.insertAuth(authData);

        AuthData retrievedAuth = authDAO.getAuth("retrievableToken");

        assertNotNull(retrievedAuth);
        assertEquals("testUser", retrievedAuth.username());
    }

    @Test
    void getAuth_Fail_NonExistentToken() throws ResponseException {
        AuthData retrievedAuth = authDAO.getAuth("nonExistentToken");

        assertNull(retrievedAuth); // Should return null since token doesn't exist
    }

    @Test
    void generateAuthToken_Success() {
        String token1 = authDAO.generateAuthToken();
        String token2 = authDAO.generateAuthToken();

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2); // Tokens should be unique
    }

    @Test
    void deleteAuth_Success() throws ResponseException {
        AuthData authData = new AuthData("deleteToken", "userToDelete");
        authDAO.insertAuth(authData);

        authDAO.deleteAuth("deleteToken");

        AuthData retrievedAuth = authDAO.getAuth("deleteToken");
        assertNull(retrievedAuth); // Should be deleted
    }

    @Test
    void deleteAuth_Fail_NonExistentToken() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonExistentToken")); // Should not throw an error
    }

    @Test
    void deleteAllAuthTokens_Success() throws ResponseException {
        authDAO.insertAuth(new AuthData("token1", "user1"));
        authDAO.insertAuth(new AuthData("token2", "user2"));

        authDAO.deleteAllAuthTokens();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }
}
