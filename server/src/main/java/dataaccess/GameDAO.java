package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {

    GameData insertGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException;

    public void deleteGame(int gameID) throws DataAccessException;

    void deleteAllGames();

}
