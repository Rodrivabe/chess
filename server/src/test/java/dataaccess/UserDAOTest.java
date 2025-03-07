package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    public static void assertUserEqual(UserData expected, UserData actual) {
        assertEquals(expected.username(), actual.username());
        assertEquals(expected.password(), actual.password());
        assertEquals(expected.email(), actual.email());
    }

    private UserDAO getDataAccess(Class<? extends UserDAO> databaseClass) throws ResponseException {
        UserDAO db;
        db = new MemoryUserDAO();
        db.deleteAllUsers();
        return db;
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class})
    void insertUser(Class<? extends UserDAO> dbClass) throws ResponseException {
        UserDAO dataAccess = getDataAccess(dbClass);

        var user = new UserData("juanito", "juanitoiscool12", "juanito@gmail.com");
        assertDoesNotThrow(() -> dataAccess.insertUser(user));
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class})
    void getUser(Class<? extends UserDAO> dbClass) throws ResponseException, DataAccessException {
        UserDAO dataAccess = getDataAccess(dbClass);

        var expectedUser = new UserData("juanito", "juanitoiscool12", "juanito@gmail.com");
        dataAccess.insertUser(expectedUser);

        var actualUser = dataAccess.getUser("juanito");

        assertNotNull(actualUser, "User should be found.");
        assertUserEqual(expectedUser, actualUser);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class})
    void updateUser(Class<? extends UserDAO> dbClass) throws ResponseException, DataAccessException {
        UserDAO dataAccess = getDataAccess(dbClass);

        var user = new UserData("juanito", "juanitoiscool12", "juanito@gmail.com");
        dataAccess.insertUser(user);

        var updatedUser = new UserData("juanito", "newpassword123", "newjuanito@gmail.com");
        dataAccess.updateUser(updatedUser.username(), updatedUser);

        var actualUser = dataAccess.getUser("juanito");

        assertNotNull(actualUser, "User should still exist after update.");
        assertUserEqual(updatedUser, actualUser);
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class})
    void deleteUser(Class<? extends UserDAO> dbClass) throws ResponseException, DataAccessException {
        UserDAO dataAccess = getDataAccess(dbClass);

        var user = new UserData("juanito", "juanitoiscool12", "juanito@gmail.com");
        dataAccess.insertUser(user);

        dataAccess.deleteUser("juanito");

        var actualUser = dataAccess.getUser("juanito");
        assertNull(actualUser, "User should be deleted.");
    }

    @ParameterizedTest
    @ValueSource(classes = {MemoryUserDAO.class})
    void deleteAllUsers(Class<? extends UserDAO> dbClass) throws ResponseException, DataAccessException {
        UserDAO dataAccess = getDataAccess(dbClass);

        dataAccess.insertUser(new UserData("juanito", "juanitoiscool12", "juanito@gmail.com"));
        dataAccess.insertUser(new UserData("pablo", "pablopassword", "pablo@gmail.com"));

        dataAccess.deleteAllUsers();

        assertNull(dataAccess.getUser("juanito"), "User 'juanito' should be deleted.");
        assertNull(dataAccess.getUser("pablo"), "User 'pablo' should be deleted.");
    }
}
