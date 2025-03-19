package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {
    private AuthDAO authDao;
    private UserService userService;
    private String authToken;

    @BeforeEach
    void setup() throws ResponseException {
        authDao = new MemoryAuthDAO();
        UserDAO userDao = new MemoryUserDAO();
        userService = new UserService(authDao, userDao);

        // Step 1: Register a new user
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password", "test@example.com");
        userService.register(registerRequest);

        // Step 2: Log in the user to get an auth token
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        LoginResult loginResult = userService.login(loginRequest);

        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());

        authToken = loginResult.authToken(); // Store the auth token for logout testing
    }

    @Test
    void logoutSuccess() throws ResponseException {
        // Step 3: Log out using the auth token
        userService.logout(authToken);

        // Step 4: Ensure the auth token is deleted
        assertNull(authDao.getAuth(authToken));
    }

    @Test
    void logoutInvalidToken() {
        // Step 3: Try to log out with an invalid token
        String invalidToken = "invalidAuthToken";

        assertThrows(ResponseException.class, () -> userService.logout(invalidToken));
    }
}
