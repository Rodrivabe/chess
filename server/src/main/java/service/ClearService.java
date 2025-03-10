package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import results.ClearResult;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;

    }

    public ClearResult clearDatabase() {

        try {
            userDAO.deleteAllUsers();
            gameDAO.deleteAllGames();
            authDAO.deleteAllAuthTokens();
            return new ClearResult(true);
        } catch (Exception e) {
            return new ClearResult(false);
        }

    }


}
