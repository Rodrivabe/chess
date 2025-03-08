package service;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import requests.CreateGameRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.RegisterResult;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();

    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws ResponseException {


        if (createGameRequest.gameName() == null || createGameRequest.gameName().isBlank()) {
            throw new ResponseException(400, "Error: bad request");

        }
        try {
            UserData user = GameDAO.getGame(createGameRequest.username());
            if (user != null) {
                throw new ResponseException(403, "Error: already taken");
            }
            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(),
                    registerRequest.email());

            userDAO.insertUser(newUser);
            String authToken = authDAO.generateAuthToken();
            AuthData newAuth = new AuthData(authToken, newUser.username());
            authDAO.insertAuth(newAuth);


            RegisterResult result = new RegisterResult(newUser.username(), newAuth.authToken());

            return result;
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: "+ e.getMessage());
        }

    }
}
