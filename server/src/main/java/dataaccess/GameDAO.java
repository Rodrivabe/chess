package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void insertGame(GameData game);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    void updateGame(int gameID, GameData updatedGame);

    int generateGameID();


    void deleteAllGames();

}
