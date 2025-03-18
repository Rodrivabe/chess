package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() throws ResponseException {
        String[] createGameTableIfNotExist = {"""
            CREATE TABLE IF NOT EXISTS games (
                gameID INT AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName TEXT NOT NULL,
                game TEXT NOT NULL
            )
            """};
        DatabaseManager.configureDataBase(createGameTableIfNotExist);
    }

    public GameData insertGame(GameData game) throws ResponseException{
        var insertGameStatement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String gameStateJson = new Gson().toJson(game.game());

        int gameID = DatabaseManager.executeUpdate(insertGameStatement, game.whiteUsername(), game.blackUsername(),game.gameName(), gameStateJson);
        return new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

    }

    public GameData getGame(int gameID) throws ResponseException{

        return null;
    }

    public Collection<GameData> listGames() throws ResponseException{

        return java.util.List.of();
    }

    public void updateGame(int gameID, GameData updatedGame) throws ResponseException{

    }

    public int generateGameID() throws ResponseException{

        return 0;
    }


    public void deleteAllGames() throws ResponseException{

    }


}
