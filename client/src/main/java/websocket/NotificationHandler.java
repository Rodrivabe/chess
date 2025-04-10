package websocket;

import server.websocket.NotificationMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notification);
}
