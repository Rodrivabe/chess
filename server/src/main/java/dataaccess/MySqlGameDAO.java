package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() throws ResponseException {
        configureDatabase();
    }

    private final String[] createGameTableIfNotExist = {"""
            CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL AUTO_INCREMENT UNIQUE,
                whiteUsername VARCHAR(255) NULL,
                blackUsername VARCHAR(255) NULL,
                gameName VARCHAR(255) NOT NULL,
                game JSON NOT NULL,
                PRIMARY KEY (gameID)
            )
            """};

    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createGameTableIfNotExist) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }


    @Override
    public GameData insertGame(GameData game) throws ResponseException{
        var insertGameStatement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String gameStateJson = new Gson().toJson(game.game());

        int gameID = DatabaseManager.executeUpdate(insertGameStatement, game.whiteUsername(), game.blackUsername(),
                game.gameName(), gameStateJson);
        if (gameID > 0) {
            System.out.println("✅ Game inserted successfully with ID: " + gameID);
        } else {
            System.err.println("❌ Game insertion failed");
        }

        return game.setGameId(gameID);


    }

    public GameData getGame(int gameID) throws ResponseException{
        try (var conn = DatabaseManager.getConnection()) {
            var getGameStatement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(getGameStatement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGames(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }



    public Collection<GameData> listGames() throws ResponseException{
        Collection<GameData> result = new ArrayList<>();
        var listGamesStatement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(listGamesStatement);
             var rs = ps.executeQuery()){
            while (rs.next()) {
                result.add(readGames(rs));
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    private GameData readGames(ResultSet rs) throws SQLException {
        String gameStateJson = rs.getString("game");
        ChessGame gameState = new Gson().fromJson(gameStateJson, ChessGame.class);


        return new GameData(
                rs.getInt("gameID"),
                rs.getString("whiteUsername"),
                rs.getString("blackUsername"),
                rs.getString("gameName"),
                gameState
        );
    }

    public void updateGame(int gameID, GameData updatedGame) throws ResponseException{
        var updateGameStatement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?  WHERE gameID = ?";
        String gameStateJson = new Gson().toJson(updatedGame.game());

        DatabaseManager.executeUpdate(updateGameStatement, updatedGame.whiteUsername(),
                updatedGame.blackUsername(), updatedGame.gameName(), gameStateJson, gameID);
    }



    public void deleteAllGames() throws ResponseException{
        var deleteGamesStatement = "TRUNCATE TABLE games";
        DatabaseManager.executeUpdate(deleteGamesStatement);
    }


}
