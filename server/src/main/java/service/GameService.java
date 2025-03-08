package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import handlers.HandlerBase;
import model.GameData;
import requests.CreateGameRequest;
import results.CreateGameResult;
import results.ListGamesResult;

import java.util.Collection;

public class GameService extends HandlerBase {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(AuthDAO authDAO,GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws ResponseException {
        verifyAuthToken(authDAO, authToken);

        ListGamesResult listGamesResult = new ListGamesResult(gameDAO.listGames());

        return  listGamesResult;

    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws ResponseException {

        verifyAuthToken(authDAO, authToken);

        if (createGameRequest.gameName() == null || createGameRequest.gameName().isBlank()) {
            throw new ResponseException(400, "Error: bad request");

        }
        try {

            GameData newGame = new GameData(gameDAO.generateGameID(), null, null, createGameRequest.gameName(), new ChessGame());

            gameDAO.insertGame(newGame);

            CreateGameResult result = new CreateGameResult(newGame.gameID());

            return result;
        } catch (ResponseException e) {
            throw new ResponseException(500, "Error: "+ e.getMessage());
        }

    }
}
