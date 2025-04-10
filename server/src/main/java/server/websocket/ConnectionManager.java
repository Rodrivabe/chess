package server.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;


public class ConnectionManager {
    private final ConcurrentHashMap<Integer, List<Connection>> gameConnections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Connection> userConnections = new ConcurrentHashMap<>();


    public void add(int gameID, String username, Session session) {
        var connection = new Connection(username, session);
        userConnections.put(username, connection);
        gameConnections.putIfAbsent(gameID, new ArrayList<>());
        gameConnections.get(gameID).add(connection);
    }

    public void remove(int gameID, String username) {
        var connectionsInGame = gameConnections.get(gameID);
        if (connectionsInGame == null) return;

        connectionsInGame.removeIf(conn -> conn.username.equals(username));
    }

    public void broadcast(int gameID, String message, String excludeUsername) {
        var connectionsInGame = gameConnections.get(gameID);
        if (connectionsInGame == null) return;

        var removeList = new ArrayList<Connection>();

        for (var c : connectionsInGame) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUsername)) {
                    try {
                        c.send(message);
                    } catch (IOException e) {
                        System.out.println("Failed to send message to " + c.username);
                    }
                }
            } else {
                removeList.add(c); // connection is closed, mark for cleanup
            }
        }

        // Clean up disconnected clients
        connectionsInGame.removeAll(removeList);
    }



    public void sendToUser(String username, String message) throws IOException {
        Connection conn = userConnections.get(username);
        if (conn != null && conn.session.isOpen()) {
            conn.send(message);
        }
    }

    public void saveSession(int gameID, String username, Session session) {
        remove(gameID, username);
        add(gameID, username, session);
    }

}
