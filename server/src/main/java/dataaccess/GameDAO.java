package dataaccess;

import model.GameData;

public interface GameDAO {

    GameData insertGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    GameData listGames() throws DataAccessException;

    void updateGame(String gameID, GameData updatedGame) throws DataAccessException;

    void clear();

}
