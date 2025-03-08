package dataaccess;

import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    GameData insertGame(GameData game) throws ResponseException;

    GameData getGame(int gameID) throws ResponseException;

    Collection<GameData> listGames() throws ResponseException;

    public void updateGame(int gameID, GameData updatedGame) throws ResponseException;

    public void deleteGame(int gameID) throws ResponseException;

    public int generateGameID() throws ResponseException;


    void deleteAllGames();

}
