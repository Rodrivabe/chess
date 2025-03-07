package dataaccess;


import exception.ResponseException;

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