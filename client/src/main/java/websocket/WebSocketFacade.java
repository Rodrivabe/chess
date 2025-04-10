package websocket;


import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws URISyntaxException, DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");
        this.notificationHandler = notificationHandler;

        this.session = container.connectToServer(this, socketURI);

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }



}
