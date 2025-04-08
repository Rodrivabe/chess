package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.ServerMessage;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try{
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            UserGameCommand.CommandType type = command.getCommandType();
            String username = getUsername(command.getAuthToken());
            int gameID = command.getGameID();
            switch (type) {
                case CONNECT:
                    connect(session, username, gameID);
                case MAKE_MOVE:
                    MakeMoveCommand move = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(username, command.getGameID(), move);
                case LEAVE:
                    LeaveCommand leave = new Gson().fromJson(message, LeaveCommand.class);
                    leaveGame(username, command.getGameID());
                case RESIGN:
                    ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
                    resign(username, resignCommand);
            }
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    String getUsername(String authToken){

    }

    private void connect(String username, Session session, int gameID) throws IOException {
        connections.add(gameID, username, session);
        var message = String.format("%s is in the shop", username);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notification);
    }
}