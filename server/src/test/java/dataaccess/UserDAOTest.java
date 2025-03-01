package dataaccess;


import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO getDataAccess(Class<? extends UserDAO> databaseClass) throws ResponseException {
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
    /**
    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void deletePet(Class<? extends DataAccess> dbClass) throws ResponseException {
        DataAccess dataAccess = getDataAccess(dbClass);

        List<Pet> expected = new ArrayList<>();
        var deletePet = dataAccess.addPet(new Pet(0, "joe", PetType.FISH));
        expected.add(dataAccess.addPet(new Pet(0, "sally", PetType.CAT)));
        expected.add(dataAccess.addPet(new Pet(0, "fido", PetType.DOG)));

        dataAccess.deletePet(deletePet.id());

        var actual = dataAccess.listPets();
        assertPetCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void deleteAllPets(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess dataAccess = getDataAccess(dbClass);

        dataAccess.addPet(new Pet(0, "joe", PetType.FISH));
        dataAccess.addPet(new Pet(0, "sally", PetType.CAT));

        dataAccess.deleteAllPets();

        var actual = dataAccess.listPets();
        assertEquals(0, actual.size());
    }


    public static void assertPetEqual(Pet expected, Pet actual) {
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.type(), actual.type());
    }

    public static void assertPetCollectionEqual(Collection<Pet> expected, Collection<Pet> actual) {
        Pet[] actualList = actual.toArray(new Pet[]{});
        Pet[] expectedList = expected.toArray(new Pet[]{});
        assertEquals(expectedList.length, actualList.length);
        for (var i = 0; i < actualList.length; i++) {
            assertPetEqual(expectedList[i], actualList[i]);
        }
    }
    **/
}