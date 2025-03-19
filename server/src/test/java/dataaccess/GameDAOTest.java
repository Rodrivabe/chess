package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {

    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setup() throws ResponseException {
        authDAO = new MySqlAuthDAO();
        userDAO = new MySqlUserDAO();
        gameDAO = new MySqlGameDAO();
        authDAO.deleteAllAuthTokens();
        userDAO.deleteAllUsers();
        gameDAO.deleteAllGames();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void createGameWithAuthenticatedUser() throws ResponseException {
        var user = new UserData("cosmo1", "GoCougars123", "cosmo1@byu.edu");
        userDAO.insertUser(user);
        var auth = new AuthData(authDAO.generateAuthToken(), user.username());
        authDAO.insertAuth(auth);

        var game = new GameData(0, null, null, "BYU Chess Match", new ChessGame());
        var insertedGame = gameDAO.insertGame(game);
        assertNotNull(insertedGame);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void createGameWithoutAuthenticationFails() {
        var gameName = "Unauthorized Game";
        var authToken = "invalidToken";

        assertThrows(ResponseException.class, () -> {
            if (authDAO.getAuth(authToken) == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }

            GameData newGame = new GameData(0, null, null, gameName, new ChessGame());
            gameDAO.insertGame(newGame);
        });
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void joinGameSuccessfully(Class<? extends GameDAO> daoClass) throws ResponseException {
        var user = new UserData("cosmo2", "Shout123", "cosmo2@byu.edu");
        userDAO.insertUser(user);
        var auth = new AuthData(authDAO.generateAuthToken(), user.username());
        authDAO.insertAuth(auth);

        var game = new GameData(0, null, null, "Friendly Match", new ChessGame());
        var insertedGame = gameDAO.insertGame(game);

        var updatedGame = new GameData(insertedGame.gameID(), user.username(), null, insertedGame.gameName(), insertedGame.game());
        gameDAO.updateGame(insertedGame.gameID(), updatedGame);

        var retrievedGame = gameDAO.getGame(insertedGame.gameID());
        assertNotNull(retrievedGame);
        assertEquals(user.username(), retrievedGame.whiteUsername());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void joinGameWithInvalidGameIDFails(Class<? extends GameDAO> daoClass) throws ResponseException {
        var user = new UserData("invalidUser", "password123", "invalid@byu.edu");
        userDAO.insertUser(user);
        var auth = new AuthData(authDAO.generateAuthToken(), user.username());
        authDAO.insertAuth(auth);

        var gameID = 999; // Nonexistent game
        assertThrows(ResponseException.class, () -> {
            GameData game = gameDAO.getGame(gameID);
            if (game == null) {
                throw new ResponseException(400, "Error: bad request");
            }
        });
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void listAllGamesWithAuthentication(Class<? extends GameDAO> daoClass) throws ResponseException {
        var user = new UserData("player1", "ohYeahIWin", "player1@byu.edu");
        userDAO.insertUser(user);
        var auth = new AuthData(authDAO.generateAuthToken(), user.username());
        authDAO.insertAuth(auth);

        gameDAO.insertGame(new GameData(0, user.username(), null, "Ranked Match", new ChessGame()));
        gameDAO.insertGame(new GameData(0, null, null, "Casual Match", new ChessGame()));

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void listAllGamesWithoutAuthenticationFails(Class<? extends GameDAO> daoClass) throws ResponseException {
        Collection<GameData> games = gameDAO.listGames();
        assertEquals(0, games.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void deleteAllGames(Class<? extends GameDAO> daoClass) throws ResponseException {
        var user = new UserData("cosmo3", "ChessChampion", "cosmo3@byu.edu");
        userDAO.insertUser(user);

        gameDAO.insertGame(new GameData(0, user.username(), null, "Elimination Round", new ChessGame()));
        gameDAO.deleteAllGames();

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(0, games.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void deleteAllGamesWhenNoneExist(Class<? extends GameDAO> daoClass) throws ResponseException {
        gameDAO.deleteAllGames();
        Collection<GameData> games = gameDAO.listGames();
        assertEquals(0, games.size());
    }
}
