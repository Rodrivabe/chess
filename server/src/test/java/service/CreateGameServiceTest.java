package service;

import dataaccess.*;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameServiceTest {
    private AuthDAO authDao;
    private GameDAO gameDao;
    private UserDAO userDao;
    private GameService gameService;
    private UserService userService;
    private String authToken;

    @BeforeEach
    void setup() throws DataAccessException, ResponseException {
        authDao = new MemoryAuthDAO();
        gameDao = new MemoryGameDAO();
        userDao = new MemoryUserDAO();

        gameService = new GameService(authDao, gameDao);
        userService = new UserService(authDao, userDao);

        // Step 1: Register a user
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterResult registerResult = userService.register(registerRequest);

        // Step 2: Log in to get an authentication token
        authToken = registerResult.authToken();
        assertNotNull(authToken);
    }

    @Test
    void createGame_success() throws ResponseException {
        // Step 3: Call createGame with a valid request
        CreateGameRequest request = new CreateGameRequest("Test Game");
        CreateGameResult result = gameService.createGame(request, authToken);

        // Step 4: Verify the game was created
        assertNotNull(result);
        assertTrue(result.gameID() > 0);

        GameData createdGame = gameDao.getGame(result.gameID());
        assertNotNull(createdGame);
        assertEquals("Test Game", createdGame.gameName());
    }

    @Test
    void createGame_invalidAuthToken() {
        // Step 5: Attempt to create a game with an invalid token
        CreateGameRequest request = new CreateGameRequest("Invalid Game");

        String invalidAuthToken = "invalidToken";
        assertThrows(ResponseException.class, () -> gameService.createGame(request, invalidAuthToken));
    }

    @Test
    void createGame_noGameName() {
        // Step 6: Attempt to create a game without a game name (invalid request)
        CreateGameRequest request = new CreateGameRequest("");

        assertThrows(ResponseException.class, () -> gameService.createGame(request, authToken));
    }
}
