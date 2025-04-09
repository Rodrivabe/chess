package websocket.messages;

import model.GameData;
import websocket.commands.UserGameCommand;

import java.util.Objects;

public class NotificationMessage extends ServerMessage {

    private final String message;

    public NotificationMessage(String message){
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.message = message;

    }
    public String getMessage() {
        return message;
    }

    public static ServerMessage getServerMessage(String username, GameData game, UserGameCommand.CommandType commandType) {
        String notifyText = "";

        switch (commandType){
            case CONNECT:
                if(!Objects.equals(game.blackUsername(), username) && !Objects.equals(game.whiteUsername(), username)){
                    notifyText = String.format("%s joined the game as an observer", username);
                }
                else if(Objects.equals(game.whiteUsername(), username)){
                    notifyText = String.format("%s joined the game as white", username);
                } else if (Objects.equals(game.blackUsername(), username)) {
                    notifyText = String.format("%s joined the game as black", username);
                }
        }


        return new NotificationMessage(notifyText);
    }
}
