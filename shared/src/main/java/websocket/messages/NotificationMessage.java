package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import websocket.commands.MakeMoveCommand;
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

    public static ServerMessage getServerMessage(String username, GameData game, UserGameCommand command) {
        String notifyText = "";
        UserGameCommand.CommandType commandType = command.getCommandType();

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
            case MAKE_MOVE:
                MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);

                ChessMove move = moveCommand.getMove();
                ChessPosition startPosition = move.getStartPosition();
                ChessPosition endPosition = move.getEndPosition();
                notifyText = String.format("%s moved %s to %s", username, startPosition, endPosition);
        }


        return new NotificationMessage(notifyText);
    }
}
