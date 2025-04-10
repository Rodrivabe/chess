package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;


import java.util.Objects;

public class NotificationMessage extends ServerMessage {

    private final String message;

    public NotificationMessage(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;

    }
    public String getMessage() {
        return message;
    }

    public static ServerMessage getServerMessage(String username, GameData game, UserGameCommand command, String message, ChessGame.TeamColor color, String check) {
        String notifyText = "";
        UserGameCommand.CommandType commandType = command.getCommandType();

        switch (commandType){
            case CONNECT:
                notifyText = switch (color) {
                    case null -> String.format("%s joined the game as an observer", username);
                    case WHITE -> String.format("%s joined the game as white", username);
                    case BLACK -> String.format("%s joined the game as black", username);
                };
                break;
            case MAKE_MOVE:
                Gson gson = new Gson();
                MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);

                ChessMove move = moveCommand.getMove();
                ChessPosition startPosition = move.getStartPosition();
                ChessPosition endPosition = move.getEndPosition();

                notifyText = switch (check) {
                    case "inCheck" -> String.format("%s is in Check", username);
                    case "inCheckMate" -> String.format("%s is in Check mate", username);
                    case "inStaleMate" -> String.format("%s is in Stale mate", username);
                    default -> String.format("%s moved %s to %s", username, startPosition, endPosition);
                };
                break;

        }


        return new NotificationMessage(notifyText);
    }
}
