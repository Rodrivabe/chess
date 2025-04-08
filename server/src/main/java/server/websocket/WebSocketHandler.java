package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try{
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            UserGameCommand.CommandType type = command.getCommandType();
            String username = getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            switch (type) {
                case CONNECT:
                    connect(username, session, gameID);
                case MAKE_MOVE:
                    makeMove(username, command.getGameID(), command);
                case LEAVE:
                    leaveGame(username, command.getGameID());
                case RESIGN:
                    resign(username, command);
            }
        } catch (JsonSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void resign(String username, UserGameCommand command) {
    }

    private void leaveGame(String username, Integer gameID) {
    }

    private void makeMove(String username, Integer gameID, UserGameCommand command) {

    }

    String getUsername(String authToken){

        return authToken;
    }

    private void connect(String username, Session session, int gameID) throws IOException {
        connections.add(gameID, username, session);


        var message = String.format("%s is in the shop", username);

        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, gson.toJson(notification), username);
    }
}