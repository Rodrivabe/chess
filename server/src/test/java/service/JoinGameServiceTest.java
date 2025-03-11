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
import static chess.ChessGame.TeamColor.BLACK;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTest {
    private AuthDAO authDao;
    private GameDAO gameDao;
    private UserDAO userDao;
    private GameService gameService;
    private UserService userService;
    private String player1_authToken;
    private String player2_authToken;
    int testGameID;


    @BeforeEach
    void setup() throws DataAccessException, ResponseException {
        authDao = new MemoryAuthDAO();
        gameDao = new MemoryGameDAO();
        userDao = new MemoryUserDAO();

        gameService = new GameService(authDao, gameDao);
        userService = new UserService(authDao, userDao);

        // Create a test user and authenticate them
        RegisterResult result1 = userService.register(new requests.RegisterRequest("player1", "password", "player1@example.com"));
        RegisterResult result2 = userService.register(new requests.RegisterRequest("player2", "password", "player2@example.com"));
        player1_authToken = result1.authToken();
        player2_authToken = result2.authToken();

        // Get authToken for the user
        AuthData authData = authDao.getAuth(player1_authToken);
        assertNotNull(authData);

        // Create a test game
        GameData testGame = new GameData(1, null, null, "Test Game", new chess.ChessGame());
        testGameID = testGame.gameID();
        gameDao.insertGame(testGame);
    }

    @Test
    void joinGame_success() throws DataAccessException, ResponseException {
        // Get the player's authToken
        AuthData authData = authDao.getAuth(player1_authToken);
        assertNotNull(authData);

        // Create the JoinGameRequest
        JoinGameRequest request = new JoinGameRequest( WHITE, testGameID);

        // Call the service
        gameService.joinGame(request, player1_authToken);

        // Assertions
        GameData updatedGame = gameDao.getGame(1);
        assertEquals("player1", updatedGame.whiteUsername());
    }

    @Test
    void joinGame_invalidGameID() throws ResponseException {
        AuthData authData = authDao.getAuth(player1_authToken);
        assertNotNull(authData);

        JoinGameRequest request = new JoinGameRequest(WHITE, 999);

        assertThrows(ResponseException.class, () -> gameService.joinGame(request, player1_authToken));
    }

    @Test
    void joinGame_spotAlreadyTaken() throws DataAccessException, ResponseException {
        // Player 1 joins as WHITE
        AuthData authData1 = authDao.getAuth(player1_authToken);
        assertNotNull(authData1);
        JoinGameRequest request1 = new JoinGameRequest(WHITE, testGameID);
        gameService.joinGame(request1, player1_authToken);

        // Player 2 tries to join the same spot
        AuthData authData2 = authDao.getAuth(player2_authToken);
        assertNotNull(authData2);
        JoinGameRequest request2 = new JoinGameRequest(WHITE, testGameID) ;

        assertThrows(ResponseException.class, () -> gameService.joinGame(request2, player2_authToken));
    }


}
