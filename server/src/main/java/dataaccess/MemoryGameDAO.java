package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    public GameData insertGame(GameData game) {
        int gameID = generateGameID();
        GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

        games.put(game.gameID(), newGame);
        return newGame;
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

    public void deleteGame(int gameID) {
        games.remove(gameID);
    }

    public void deleteAllGames() {
        games.clear();
        nextId = 1;

    }


}
