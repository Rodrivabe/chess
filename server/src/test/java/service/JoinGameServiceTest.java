package service;

import dataaccess.*;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.JoinGameRequest;
import results.RegisterResult;
import model.AuthData;
import model.GameData;

import static chess.ChessGame.TeamColor.WHITE;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTest {
    private AuthDAO authDao;
    private GameDAO gameDao;
    private GameService gameService;
    private String player1AuthToken;
    private String player2AuthToken;
    int testGameID;


    @BeforeEach
    void setup() throws ResponseException {
        authDao = new MemoryAuthDAO();
        gameDao = new MemoryGameDAO();
        UserDAO userDao = new MemoryUserDAO();

        gameService = new GameService(authDao, gameDao);
        UserService userService = new UserService(authDao, userDao);

        // Create a test user and authenticate them
        RegisterResult result1 = userService.register(new requests.RegisterRequest("player1", "password", "player1@example.com"));
        RegisterResult result2 = userService.register(new requests.RegisterRequest("player2", "password", "player2@example.com"));
        player1AuthToken = result1.authToken();
        player2AuthToken = result2.authToken();

        // Get authToken for the user
        AuthData authData = authDao.getAuth(player1AuthToken);
        assertNotNull(authData);

        // Create a test game
        GameData testGame = new GameData(1, null, null, "Test Game", new chess.ChessGame());
        testGameID = testGame.gameID();
        gameDao.insertGame(testGame);
    }

    @Test
    void joinGameSuccess() throws ResponseException {
        // Get the player's authToken
        AuthData authData = authDao.getAuth(player1AuthToken);
        assertNotNull(authData);

        // Create the JoinGameRequest
        JoinGameRequest request = new JoinGameRequest( WHITE, testGameID);

        // Call the service
        gameService.joinGame(request, player1AuthToken);

        // Assertions
        GameData updatedGame = gameDao.getGame(1);
        assertEquals("player1", updatedGame.whiteUsername());
    }

    @Test
    void joinGameInvalidGameID() {
        AuthData authData = authDao.getAuth(player1AuthToken);
        assertNotNull(authData);

        JoinGameRequest request = new JoinGameRequest(WHITE, 999);

        assertThrows(ResponseException.class, () -> gameService.joinGame(request, player1AuthToken));
    }

    @Test
    void joinGameSpotAlreadyTaken() throws ResponseException {
        // Player 1 joins as WHITE
        AuthData authData1 = authDao.getAuth(player1AuthToken);
        assertNotNull(authData1);
        JoinGameRequest request1 = new JoinGameRequest(WHITE, testGameID);
        gameService.joinGame(request1, player1AuthToken);

        // Player 2 tries to join the same spot
        AuthData authData2 = authDao.getAuth(player2AuthToken);
        assertNotNull(authData2);
        JoinGameRequest request2 = new JoinGameRequest(WHITE, testGameID) ;

        assertThrows(ResponseException.class, () -> gameService.joinGame(request2, player2AuthToken));
    }


}
