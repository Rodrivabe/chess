package server.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;


public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> userConnections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, List<Connection>> gameConnections = new ConcurrentHashMap<>();


    public void add(int gameID, String username, Session session) {
        var connection = new Connection(username, session);
        userConnections.put(username, connection);
        gameConnections.putIfAbsent(gameID, new ArrayList<>());
        gameConnections.get(gameID).add(connection);
    }

    public void remove(int gameID) {

    }

    public void broadcast(int gameID, String json, String username) {
    }

    public void sendToUser(String username, String message) throws IOException {
        Connection conn = userConnections.get(username);
        if (conn != null && conn.session.isOpen()) {
            conn.send(message);
        }
    }

    public void saveSession(int gameID, String username, Session session) {
        add(gameID, username, session);
    }

}
