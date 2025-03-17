package dataaccess;

import exception.ResponseException;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {

    public void insertGame(GameData game){

    }

    public GameData getGame(int gameID){

        return null;
    }

    public Collection<GameData> listGames(){

        return java.util.List.of();
    }

    public void updateGame(int gameID, GameData updatedGame){

    }

    public int generateGameID(){

        return 0;
    }


    public void deleteAllGames(){

    }


}
