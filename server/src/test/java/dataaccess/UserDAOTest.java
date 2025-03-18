package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mindrot.jbcrypt.BCrypt;

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
    @ValueSource(classes = {MySqlUserDAO.class})
    void insertUser(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        var user = new UserData("john_doe", "securepassword", "john@example.com");
        userDAO.insertUser(user);

        var retrievedUser = userDAO.getUser("john_doe");

        // Verify username & email are stored correctly
        assertEquals(user.username(), retrievedUser.username());
        assertEquals(user.email(), retrievedUser.email());

        // Verify the password is NOT stored in plaintext
        assertNotEquals("securepassword", retrievedUser.password());

        // Verify that BCrypt correctly matches the stored hash
        assertTrue(BCrypt.checkpw("securepassword", retrievedUser.password()));
    }


    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void listUsers(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        // Insert users
        var user1 = new UserData("alice", "password1", "alice@example.com");
        var user2 = new UserData("bob", "password2", "bob@example.com");
        userDAO.insertUser(user1);
        userDAO.insertUser(user2);

        // Fetch list of users
        var users = userDAO.listUsers();

        // Check size
        assertEquals(2, users.size());

        // Convert collection to a list for easier verification
        var userList = new ArrayList<>(users);

        // Check usernames and emails
        assertEquals("alice", userList.get(0).username());
        assertEquals("alice@example.com", userList.get(0).email());
        assertEquals("bob", userList.get(1).username());
        assertEquals("bob@example.com", userList.get(1).email());

        // Ensure passwords are hashed
        assertNotEquals("password1", userList.get(0).password());
        assertNotEquals("password2", userList.get(1).password());

        // Verify stored passwords match expected ones using BCrypt
        assertTrue(BCrypt.checkpw("password1", userList.get(0).password()));
        assertTrue(BCrypt.checkpw("password2", userList.get(1).password()));
    }


    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void getUser(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        var user = new UserData("sam", "mypassword", "sam@example.com");
        userDAO.insertUser(user);

        var retrievedUser = userDAO.getUser("sam");

        assertEquals(user.username(), retrievedUser.username());
        assertEquals(user.email(), retrievedUser.email());

        // Password should be hashed
        assertNotEquals("mypassword", retrievedUser.password());
        assertTrue(BCrypt.checkpw("mypassword", retrievedUser.password()));

        // Ensure a non-existent user returns null
        assertNull(userDAO.getUser("does_not_exist"));
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
