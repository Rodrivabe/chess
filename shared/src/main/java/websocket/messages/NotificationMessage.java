package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;
import server.websocket.ConnectionManager;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

public class NotificationMessage extends ServerMessage {

    private final String message;
    private static final Gson gson = new Gson();

    public NotificationMessage(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;

    }
    public String getMessage() {
        return message;
    }

    public static NotificationMessage getServerMessage(String username, GameData game, UserGameCommand command,
                                                       String message, ChessGame.TeamColor color, String check) {
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
            case LEAVE:
                notifyText = String.format("%s has left the game", username);
                break;
            case RESIGN:
                notifyText = String.format("%s has resigned the game", username);
                break;



        }






        return new NotificationMessage(notifyText);
    }

    public static void sendNotification(NotificationMessage notification, ConnectionManager connections, int gameID, String username){
        String notificationJsonInMate = gson.toJson(notification);
        connections.broadcast(gameID, notificationJsonInMate, username);
    }
}
