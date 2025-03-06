import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import requests.RegisterRequest;
import results.RegisterResult;
import service.AuthService;
import service.UserService;

class RegisterServiceTest {
    private AuthDAO authDao;
    private UserDAO userDao;
    private UserService userService;

    @BeforeEach
    void setup() throws DataAccessException {
        authDao = new MemoryAuthDAO();
        userDao = new MemoryUserDAO();
        userService = new UserService(userDao, authDao);
    }

    @Test
    void registerUser_success() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("newUser", "securePassword", "newuser@example.com");
        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(authDao.getAuth(result.getAuthToken()));
        assertNotNull(userDao.getUser("newUser"));
    }

    @Test
    void registerUser_duplicateUsername() throws DataAccessException {
        RegisterRequest request1 = new RegisterRequest("existingUser", "password123", "user1@example.com");
        userService.register(request1);

        RegisterRequest request2 = new RegisterRequest("existingUser", "newpassword", "user2@example.com");
        RegisterResult result = userService.register(request2);

        assertFalse(result.isSuccess());
    }

    @Test
    void registerUser_invalidData() {
        RegisterRequest invalidRequest = new RegisterRequest("", "", "");
        RegisterResult result = userService.register(invalidRequest);

        assertFalse(result.isSuccess());
    }
}