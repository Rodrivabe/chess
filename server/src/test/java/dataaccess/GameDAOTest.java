package dataaccess;


import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {

    private GameDAO getDataAccess(Class<? extends GameDAO> databaseClass) throws ResponseException {
        GameDAO db;

        db = new MemoryGameDAO();

        db.deleteAllGames();
        return db;
    }
/**
    @ParameterizedTest
    @ValueSource(classes = {MemoryGameDAO.class})
    void addGame(Class<? extends GameDAO> dbClass) throws ResponseException {
        GameDAO dataAccess = getDataAccess(dbClass);

        var game = new GameData(1234, "juanitoiscool12", "pedritoiscool90", );
        assertDoesNotThrow(() -> dataAccess.insertGame(game));
    }
**/

}