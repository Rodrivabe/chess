package service;

import dataaccess.*;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    private AuthDAO authDao;
    private UserService userService;

    @BeforeEach
    void setup() {
        authDao = new MemoryAuthDAO();
        UserDAO userDao = new MemoryUserDAO();
        userService = new UserService(authDao, userDao);
    }

    @Test
    void loginUser_success() throws ResponseException {
        // Register a user first
        userService.register(new requests.RegisterRequest("testUser", "securePass", "test@example.com"));

        // Attempt login
        LoginRequest loginRequest = new LoginRequest("testUser", "securePass");
        LoginResult loginResult = userService.login(loginRequest);

        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken()); // Ensure authToken is returned
        assertNotNull(authDao.getAuth(loginResult.authToken()));
    }

    @Test
    void loginUser_invalidPassword() throws ResponseException {
        // Register a user
        userService.register(new requests.RegisterRequest("testUser", "securePass", "test@example.com"));

        // Attempt login with incorrect password
        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPass");

        assertThrows(ResponseException.class, () -> userService.login(loginRequest));
    }

    @Test
    void loginUser_nonExistentUser() {
        // Attempt login with a username that was never registered
        LoginRequest loginRequest = new LoginRequest("nonUser", "somePass");

        assertThrows(ResponseException.class, () -> userService.login(loginRequest));
    }
}
