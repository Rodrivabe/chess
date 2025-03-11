package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    public void insertGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public int generateGameID(){
        return nextId++;
    }

    public void updateGame(int gameID, GameData updatedGame) {
        games.put(gameID, updatedGame);

    }

    public void deleteAllGames() {
        games.clear();
        nextId = 1;

    }


}
