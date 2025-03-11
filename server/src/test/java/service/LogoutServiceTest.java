package service;

import dataaccess.*;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {
    private AuthDAO authDao;
    private UserDAO userDao;
    private UserService userService;
    private String authToken;

    @BeforeEach
    void setup() throws DataAccessException, ResponseException {
        authDao = new MemoryAuthDAO();
        userDao = new MemoryUserDAO();
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
    void logout_success() throws ResponseException {
        // Step 3: Log out using the auth token
        userService.logout(authToken);

        // Step 4: Ensure the auth token is deleted
        assertNull(authDao.getAuth(authToken));
    }

    @Test
    void logout_invalidToken() {
        // Step 3: Try to log out with an invalid token
        String invalidToken = "invalidAuthToken";

        assertThrows(ResponseException.class, () -> userService.logout(invalidToken));
    }
}
