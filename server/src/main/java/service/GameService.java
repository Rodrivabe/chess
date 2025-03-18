package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import handlers.HandlerBase;
import model.AuthData;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import results.CreateGameResult;
import results.ListGamesResult;

import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;

public class GameService extends HandlerBase {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws ResponseException {
        verifyAuthToken(authDAO, authToken);

        return new ListGamesResult(gameDAO.listGames());

    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws ResponseException {

        verifyAuthToken(authDAO, authToken);

        if (createGameRequest.gameName() == null || createGameRequest.gameName().isBlank()) {
            throw new ResponseException(400, "Error: bad request");

        }

        GameData newGame = new GameData(0, null, null, createGameRequest.gameName(), new ChessGame());

        gameDAO.insertGame(newGame);

        return new CreateGameResult(newGame.gameID());

    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws ResponseException {
        ChessGame.TeamColor color = joinGameRequest.playerColor();
        int gameID = joinGameRequest.gameID();
        verifyAuthToken(authDAO, authToken);

        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        if( color != WHITE && color != BLACK){
            throw new ResponseException(400, "Error: That is not a valid color");
        }

        if (color == WHITE && game.whiteUsername() != null && !game.whiteUsername().isEmpty()) {
            throw new ResponseException(403, "Error: White already taken");
        }
        if (color == BLACK && game.blackUsername() != null && !game.blackUsername().isEmpty()) {
            throw new ResponseException(403, "Error: Black already taken");
        }


        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();

        GameData updatedGame;
        if (color == WHITE) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        gameDAO.updateGame(gameID, updatedGame);


    }

    public Collection<GameData> listAllGames() throws ResponseException {
        return gameDAO.listGames();

    }


}
