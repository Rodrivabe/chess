package dataaccess;

import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    GameData insertGame(GameData game) throws ResponseException;;

    GameData getGame(int gameID) throws ResponseException;;

    Collection<GameData> listGames() throws ResponseException;;

    void updateGame(int gameID, GameData updatedGame) throws ResponseException;;

    void deleteAllGames() throws ResponseException;;

}
