package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {

    private UserDAO userDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setup() throws ResponseException {
        AuthDAO authDAO = new MySqlAuthDAO();
        userDAO = new MySqlUserDAO();
        gameDAO = new MySqlGameDAO();
        authDAO.deleteAllAuthTokens();
        userDAO.deleteAllUsers();
        gameDAO.deleteAllGames();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void createGameWithRegisteredUsers(Class<? extends GameDAO> daoClass) throws ResponseException {
        var whiteUser = new UserData("cosmo1", "GoCougars123", "cosmo1@byu.edu");
        var blackUser = new UserData("cosmo2", "RiseAndShout", "cosmo2@byu.edu");
        userDAO.insertUser(whiteUser);
        userDAO.insertUser(blackUser);

        var game = new GameData(0, whiteUser.username(), blackUser.username(), "Our Game", new ChessGame());
        assertDoesNotThrow(() -> gameDAO.insertGame(game));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void getGameById(Class<? extends GameDAO> daoClass) throws ResponseException {
        var user = new UserData("cosmo3", "nonono", "cosmo3@byu.edu");
        userDAO.insertUser(user);

        var game = new GameData(0, user.username(), null, "MyChess Game", new ChessGame());
        var insertedGame = gameDAO.insertGame(game);

        var retrievedGame = gameDAO.getGame(insertedGame.gameID());
        assertNotNull(retrievedGame);
        assertEquals(game.gameName(), retrievedGame.gameName());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void listAllGames(Class<? extends GameDAO> daoClass) throws ResponseException {
        var user1 = new UserData("player1", "ohYeahIWin", "player1@byu.edu");
        var user2 = new UserData("player2", "IAmBetter", "player2@byu.edu");
        userDAO.insertUser(user1);
        userDAO.insertUser(user2);

        gameDAO.insertGame(new GameData(0, user1.username(), user2.username(), "There is this game", new ChessGame()));
        gameDAO.insertGame(new GameData(0, user2.username(), null, "There is another game", new ChessGame()));

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlGameDAO.class})
    void deleteAllGames(Class<? extends GameDAO> daoClass) throws ResponseException {
        var user = new UserData("cosmo1", "NoOneCanBeatMe", "cosmo1@byu.edu");
        userDAO.insertUser(user);

        gameDAO.insertGame(new GameData(0, user.username(), null, "ChessGame", new ChessGame()));
        gameDAO.deleteAllGames();

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(0, games.size());
    }
}
