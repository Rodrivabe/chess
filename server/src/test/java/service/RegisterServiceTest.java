package service;

import dataaccess.*;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTest {
    private AuthDAO authDao;
    private UserDAO userDao;
    private UserService userService;

    @BeforeEach
    void setup() {
        authDao = new MemoryAuthDAO();
        userDao = new MemoryUserDAO();
        userService = new UserService(authDao, userDao);
    }

    @Test
    void registerUser_success() throws DataAccessException, ResponseException {
        RegisterRequest request = new RegisterRequest("newUser", "securePassword", "newuser@example.com");
        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertNotNull(result.authToken()); // Ensure authToken was created
        assertNotNull(authDao.getAuth(result.authToken()));
        assertNotNull(userDao.getUser("newUser"));
    }

    @Test
    void registerUser_duplicateUsername() throws DataAccessException, ResponseException {
        RegisterRequest request1 = new RegisterRequest("existingUser", "password123", "user1@example.com");
        userService.register(request1);

        RegisterRequest request2 = new RegisterRequest("existingUser", "newpassword", "user2@example.com");

        assertThrows(ResponseException.class, () -> userService.register(request2));
    }

    @Test
    void registerUser_invalidData() {
        RegisterRequest invalidRequest = new RegisterRequest("", "", "");

        assertThrows(ResponseException.class, () -> userService.register(invalidRequest));
    }
}