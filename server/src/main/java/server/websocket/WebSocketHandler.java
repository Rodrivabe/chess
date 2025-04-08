package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
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
                case CONNECT -> connect(username, session, gameID);
                case MAKE_MOVE -> makeMove(username, command.getGameID(), command);
                case LEAVE -> leaveGame(username, command.getGameID());
                case RESIGN -> resign(username, command);
            }
        } catch (JsonSyntaxException | IOException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + e.getMessage()));

        } catch (ResponseException e) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        }

    }

    private void resign(String username, UserGameCommand command) {
    }

    private void leaveGame(String username, Integer gameID) {
    }

    private void makeMove(String username, Integer gameID, UserGameCommand command) {

    }

    String getUsername(String authToken) throws ResponseException{

        try{
            var auth = authDAO.getAuth(authToken);
            return auth.username();
        }catch (ResponseException e){
            throw new ResponseException(e.statusCode(), e.getMessage());
        }
    }

    private void connect(String username, Session session, int gameID) throws IOException {
        String gameJson = loadGameFromDatabase(gameID);
        ServerMessage loadGameMsg = new LoadGameMessage(gameJson);
        String loadGameJson = gson.toJson(loadGameMsg);
        connections.sendToUser(username, loadGameJson);

        String notifyText = String.format("%s joined the game", username);
        ServerMessage notification = new NotificationMessage(notifyText);
        String notificationJson = gson.toJson(notification);
        connections.broadcast(gameID,notificationJson, username);
    }


    private String loadGameFromDatabase(int gameID) {
        try {
            GameData game = gameService.getGame(gameID);
            return gson.toJson(game);
        }catch (ResponseException e){
            System.out.println("Failed to load game: " + e.getMessage());
            return "{}";
        }


    }

    private void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        String json = gson.toJson(message);
        remote.sendString(json);
    }
}