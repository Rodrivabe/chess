package dataaccess;


import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {

    private GameDAO getDataAccess(Class<? extends UserDAO> databaseClass) throws ResponseException {
        UserDAO db;

        db = new MemoryUserDAO();

        db.deleteAllUsers();
        return db;
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class})
    void addUser(Class<? extends UserDAO> dbClass) throws ResponseException {
        UserDAO dataAccess = getDataAccess(dbClass);

        var user = new UserData("juanito", "juanitoiscool12", "juanito@gmail.com");
        assertDoesNotThrow(() -> dataAccess.insertUser(user));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class})
    void listPets(Class<? extends UserDAO> dbClass) throws ResponseException {
        UserDAO dataAccess = getDataAccess(dbClass);

        List<UserData> expected = new ArrayList<>();
        expected.add(dataAccess.addPet(new Pet(0, "joe", PetType.FISH)));
        expected.add(dataAccess.addPet(new Pet(0, "sally", PetType.CAT)));
        expected.add(dataAccess.addPet(new Pet(0, "fido", PetType.DOG)));

        var actual = dataAccess.listPets();
        assertPetCollectionEqual(expected, actual);
    }
}