package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
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
    private final GameService gameService;

    public WebSocketHandler(AuthDAO authDAO, GameService gameService){
        this.authDAO = authDAO;
        this.gameService = gameService;
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
                case CONNECT -> connect(username, session, gameID, type);
                case MAKE_MOVE -> makeMove(username, command.getGameID(), command, type);
                case LEAVE -> leaveGame(username, command.getGameID(), type);
                case RESIGN -> resign(username, command, type);
            }
        } catch (JsonSyntaxException | IOException | ResponseException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + e.getMessage()));
        }

    }

    private void resign(String username, UserGameCommand command, UserGameCommand.CommandType type) {
    }

    private void leaveGame(String username, Integer gameID, UserGameCommand.CommandType type) {
    }

    String getUsername(String authToken) throws ResponseException {
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Invalid auth token");
        }
        return auth.username();
    }

    private void connect(String username, Session session, int gameID, UserGameCommand.CommandType type) throws IOException, ResponseException {
        GameData game;
        try {
            game = gameService.getGame(gameID);
        }catch (ResponseException e){
            throw new ResponseException(e.statusCode(), e.getMessage());
        }
        String gameJson = gson.toJson(game);
        ServerMessage loadGameMsg = new LoadGameMessage(gameJson);
        String loadGameJson = gson.toJson(loadGameMsg);
        connections.sendToUser(username, loadGameJson);

        ServerMessage notification = NotificationMessage.getServerMessage(username, game, type);
        String notificationJson = gson.toJson(notification);

        connections.broadcast(gameID,notificationJson, username);
    }


    private void makeMove(String username, Integer gameID, UserGameCommand command, UserGameCommand.CommandType type) {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(command)
    }

    private void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        String json = gson.toJson(message);
        remote.sendString(json);
    }
}