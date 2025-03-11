package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClearServiceTest {
    private AuthDAO AuthDao;
    private GameDAO GameDao;
    private UserDAO UserDao;
    private ClearService clearService;
    private UserService userService;
    private GameService gameService;


    @BeforeEach
    void createDAOS() {
        AuthDao = new MemoryAuthDAO();
        GameDao = new MemoryGameDAO();
        UserDao = new MemoryUserDAO();
        clearService = new ClearService(AuthDao, UserDao, GameDao);
        userService = new UserService(AuthDao, UserDao);
        gameService = new GameService(AuthDao, GameDao);

    }

    @Test
    void clearDatabase() {

        UserDao.insertUser(new UserData("player1", "password", "heyyou@byu.edu"));
        UserDao.insertUser(new UserData("player2", "password", "heyyoutwo@byu.edu"));
        GameDao.insertGame(new GameData(1, "player1", "player2", "You are dead", new chess.ChessGame()));
        AuthDao.insertAuth(new AuthData("authToken1", "hello"));

        assertNotNull(UserDao.getUser("player1"));
        assertNotNull(AuthDao.getAuth("authToken1"));
        assertNotNull(GameDao.getGame(1));


        clearService.clearDatabase();

        assertEquals(0, userService.listAllUsers().size());
        assertEquals(0, gameService.listAllGames().size());
    }
}