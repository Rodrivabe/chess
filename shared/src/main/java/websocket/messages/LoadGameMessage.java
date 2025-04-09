package websocket.messages;

import com.google.gson.Gson;
import model.GameData;
import server.websocket.ConnectionManager;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class LoadGameMessage extends ServerMessage {
    private final String game;

    public LoadGameMessage(String game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
    public String getGame(){
        return game;
    }

    public static void sendLoadGameMessage(Gson gson, GameData gameData, ConnectionManager connections, String username, UserGameCommand command, String colorFlag) throws IOException {
        UserGameCommand.CommandType commandType = command.getCommandType();

        String gameJson = gson.toJson(gameData);
        ServerMessage loadGameMsg = new LoadGameMessage(gameJson);
        String loadGameJson = gson.toJson(loadGameMsg);
        switch (commandType){
            case CONNECT -> connections.sendToUser(username, loadGameJson);
            case MAKE_MOVE -> {
                connections.sendToUser(username, loadGameJson);
                connections. broadcast(gameData.gameID(), loadGameJson, username);
            }
        }
    }
}
