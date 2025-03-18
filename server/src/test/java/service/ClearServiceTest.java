package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClearServiceTest {
    private AuthDAO authDao;
    private GameDAO gameDao;
    private UserDAO userDao;
    private ClearService clearService;
    private UserService userService;
    private GameService gameService;


    @BeforeEach
    void createDAOS() {
        authDao = new MemoryAuthDAO();
        gameDao = new MemoryGameDAO();
        userDao = new MemoryUserDAO();
        clearService = new ClearService(authDao, userDao, gameDao);
        userService = new UserService(authDao, userDao);
        gameService = new GameService(authDao, gameDao);

    }

    @Test
    void clearDatabase() throws ResponseException {

        userDao.insertUser(new UserData("player1", "password", "heyyou@byu.edu"));
        userDao.insertUser(new UserData("player2", "password", "heyyoutwo@byu.edu"));
        gameDao.insertGame(new GameData(1, "player1", "player2", "You are dead", new chess.ChessGame()));
        authDao.insertAuth(new AuthData("authToken1", "hello"));

        assertNotNull(userDao.getUser("player1"));
        assertNotNull(authDao.getAuth("authToken1"));
        assertNotNull(gameDao.getGame(1));


        clearService.clearDatabase();

        assertEquals(0, userService.listAllUsers().size());
        assertEquals(0, gameService.listAllGames().size());
    }
}