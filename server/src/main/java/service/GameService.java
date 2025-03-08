package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import handlers.HandlerBase;
import model.AuthData;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import results.CreateGameResult;
import results.ListGamesResult;

import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;

public class GameService extends HandlerBase {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
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

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws ResponseException {
        String color = joinGameRequest.playerColor();
        int gameID = joinGameRequest.gameID();
        verifyAuthToken(authDAO, authToken);

        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        if(!Objects.equals(color, "WHITE") || !Objects.equals(color, "BLACK")){
            throw new ResponseException(400, "Error: That is not a valid color");
        }

        if (Objects.equals(color, "WHITE") && game.whiteUsername() != null && !game.whiteUsername().isEmpty()) {
            throw new ResponseException(403, "Error: White already taken");
        }
        if (Objects.equals(color, "Black") && game.blackUsername() != null && !game.blackUsername().isEmpty()) {
            throw new ResponseException(403, "Error: Black already taken");
        }



        try {

            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();

            if (Objects.equals(color, "WHITE")) {
                GameData updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                gameDAO.updateGame(gameID, updatedGame);
            } else if (Objects.equals(color, "BLACK")) {
                GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                gameDAO.updateGame(gameID, updatedGame);
            }

        } catch (ResponseException e) {
            throw new ResponseException(500, "Error: "+ e.getMessage());
        }

    }
}
