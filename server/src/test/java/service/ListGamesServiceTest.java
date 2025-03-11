package service;

import dataaccess.*;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.RegisterRequest;
import results.ListGamesResult;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class ListGamesServiceTest {
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
        authToken = registerResult.authToken();
        // Step 2: Log in to get an authentication token

        assertNotNull(authToken);

        // Step 3: Create multiple games
        gameService.createGame(new CreateGameRequest("Game 1"), authToken);
        gameService.createGame(new CreateGameRequest("Game 2"), authToken);
        gameService.createGame(new CreateGameRequest("Game 3"), authToken);
    }

    @Test
    void listGames_success() throws ResponseException {
        // Step 4: Call listGames and verify the number of games
        ListGamesResult result = gameService.listGames(authToken);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void listGames_invalidAuthToken() {
        // Step 5: Attempt to list games with an invalid authentication token
        String invalidAuthToken = "invalidToken";

        assertThrows(ResponseException.class, () -> gameService.listGames(invalidAuthToken));
    }

    @Test
    void listGames_noGamesAvailable() throws ResponseException {
        // Step 6: Clear all games and check if list is empty
        gameDao.deleteAllGames();

        ListGamesResult result = gameService.listGames(authToken);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
