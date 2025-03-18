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

    private UserDAO getUserDAO(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO dao;
        if (daoClass.equals(MySqlUserDAO.class)) {
            dao = new MySqlUserDAO();
        } else {
            dao = new MemoryUserDAO(); // If you have an in-memory version for testing
        }
        dao.deleteAllUsers();
        return dao;
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})  // Add MemoryUserDAO.class if it exists
    void insertUser(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        var user = new UserData("john_doe", "securepassword", "john@example.com");
        assertDoesNotThrow(() -> userDAO.insertUser(user));

        var retrievedUser = userDAO.getUser("john_doe");
        assertUserEqual(user, retrievedUser);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void listUsers(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        List<UserData> expected = new ArrayList<>();
        expected.add(new UserData("alice", "pass123", "alice@example.com"));
        expected.add(new UserData("bob", "mypassword", "bob@example.com"));
        expected.add(new UserData("charlie", "1234abcd", "charlie@example.com"));

        for (UserData user : expected) {
            userDAO.insertUser(user);
        }

        var actual = userDAO.listUsers();
        assertUserCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void getUser(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        var user = new UserData("sam", "password123", "sam@example.com");
        userDAO.insertUser(user);

        var retrievedUser = userDAO.getUser("sam");
        assertUserEqual(user, retrievedUser);

        // Check for a non-existent user
        assertNull(userDAO.getUser("non_existent_user"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void deleteUser(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        var user1 = new UserData("delete_me", "to_be_deleted", "delete@example.com");
        var user2 = new UserData("keep_me", "safe_password", "keep@example.com");

        userDAO.insertUser(user1);
        userDAO.insertUser(user2);

        userDAO.deleteAllUsers();

        assertNull(userDAO.getUser("delete_me"));
        assertNull(userDAO.getUser("keep_me"));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void insertDuplicateUser(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        var user = new UserData("duplicate", "samepassword", "duplicate@example.com");
        userDAO.insertUser(user);

        assertThrows(Exception.class, () -> userDAO.insertUser(user)); // Should fail on duplicate insert
    }

    // Helper method to compare users
    private static void assertUserEqual(UserData expected, UserData actual) {
        assertNotNull(actual);
        assertEquals(expected.username(), actual.username());
        assertEquals(expected.password(), actual.password());
        assertEquals(expected.email(), actual.email());
    }

    // Helper method to compare user collections
    private static void assertUserCollectionEqual(Collection<UserData> expected, Collection<UserData> actual) {
        UserData[] expectedArray = expected.toArray(new UserData[0]);
        UserData[] actualArray = actual.toArray(new UserData[0]);

        assertEquals(expectedArray.length, actualArray.length);
        for (int i = 0; i < expectedArray.length; i++) {
            assertUserEqual(expectedArray[i], actualArray[i]);
        }
    }
}
