package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO getUserDAO(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO dao;
        if (daoClass.equals(MySqlUserDAO.class)) {
            dao = new MySqlUserDAO();
        } else {
            dao = new MemoryUserDAO();
        }
        dao.deleteAllUsers();
        return dao;
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void insertUser(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        var user = new UserData("cosmo1", "Shout123", "cosmo@byu.edu");
        assertDoesNotThrow(() -> userDAO.insertUser(user));

        var retrievedUser = userDAO.getUser("cosmo1");
        assertNotNull(retrievedUser);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void insertDuplicateUser(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);
        var user = new UserData("cosmo2", "RiseShout", "cosmo2@byu.edu");
        userDAO.insertUser(user);
        assertThrows(ResponseException.class, () -> userDAO.insertUser(user));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void getUser(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);
        var user = new UserData("cosmo3", "GoCougars456", "brigham@byu.edu");
        userDAO.insertUser(user);

        var retrievedUser = userDAO.getUser("cosmo3");
        assertNotNull(retrievedUser);
        assertEquals(user.username(), retrievedUser.username());
        assertEquals(user.email(), retrievedUser.email());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void getUserNotFound(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        var retrievedUser = userDAO.getUser("nonexistentUser");
        assertNull(retrievedUser);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void listUsers(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);
        var user1 = new UserData("player1", "password123", "player1@byu.edu");
        var user2 = new UserData("player2", "passwords123", "player2@byu.edu");

        userDAO.insertUser(user1);
        userDAO.insertUser(user2);

        var users = userDAO.listUsers();
        assertEquals(2, users.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void listUsersEmpty(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);

        var users = userDAO.listUsers();
        assertEquals(0, users.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlUserDAO.class})
    void deleteAllUsers(Class<? extends UserDAO> daoClass) throws ResponseException {
        UserDAO userDAO = getUserDAO(daoClass);
        var user = new UserData("newPlayer", "BYU12345", "newPlayer@byu.edu");

        userDAO.insertUser(user);
        userDAO.deleteAllUsers();

        var users = userDAO.listUsers();
        assertEquals(0, users.size());
    }
}
