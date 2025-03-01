package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private int nextId = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();


    public GameData insertGame(GameData game) {
        game = new GameData(nextId++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

        games.put(game.gameID(), game);
        return game;
    }

    public GameData getGame(int gameID) {
        return null;
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void updateGame(String gameID, GameData updatedGame) {

    }

    public void deleteAllGames() {
        games.clear();

    }


}
