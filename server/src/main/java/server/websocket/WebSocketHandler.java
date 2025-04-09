package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final GameService gameService;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, GameService gameService){
        this.authDAO = authDAO;
        this.gameService = gameService;
        this.gameDAO = gameDAO;
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try{
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            UserGameCommand.CommandType type = command.getCommandType();
            String username = getUsername(command.getAuthToken());

            connections.saveSession(command.getGameID(), username, session);

            int gameID = command.getGameID();
            switch (type) {
                case CONNECT -> connect(username, session, command);
                case MAKE_MOVE -> makeMove(username, command, message);
                case LEAVE -> leaveGame(username, command);
                case RESIGN -> resign(username, command);
            }
        } catch (JsonSyntaxException | IOException | ResponseException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + e.getMessage()));
        }

    }

    private void resign(String username, UserGameCommand command) {
    }

    private void leaveGame(String username, UserGameCommand command) {
    }

    String getUsername(String authToken) throws ResponseException {
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Invalid auth token");
        }
        return auth.username();
    }

    private void connect(String username, Session session, UserGameCommand command) throws IOException, ResponseException {
        GameData game;
        int gameID = command.getGameID();
        try {
            game = gameService.getGame(gameID);
        }catch (ResponseException e){
            throw new ResponseException(e.statusCode(), e.getMessage());
        }

        LoadGameMessage.sendLoadGameMessage(gson, game, connections, username, command);


        ServerMessage notification = NotificationMessage.getServerMessage(username, game, command);
        String notificationJson = gson.toJson(notification);

        connections.broadcast(gameID, notificationJson, username);
    }


    private void makeMove(String username, UserGameCommand command, String message) throws RuntimeException {
        GameData game;
        int gameID = command.getGameID();
        MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);


        try {
            game = gameService.getGame(gameID);
            ChessGame chessGame = game.game();
            chessGame.makeMove(moveCommand.getMove());
            GameData updatedGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

            gameDAO.updateGame(gameID, updatedGame);

            LoadGameMessage.sendLoadGameMessage(gson, updatedGame, connections, username, command);

            ServerMessage notification = NotificationMessage.getServerMessage(username, game, command);
            String notificationJson = gson.toJson(notification);
            connections.broadcast(gameID, notificationJson, username);


        } catch (ResponseException | InvalidMoveException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }




    }

    private void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        String json = gson.toJson(message);
        remote.sendString(json);
    }
}