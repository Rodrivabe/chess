package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MySqlAuthDAOTest {

    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setup() throws ResponseException {
        authDAO = new MySqlAuthDAO();
        userDAO = new MySqlUserDAO();
        gameDAO = new MySqlGameDAO();
        authDAO.deleteAllAuthTokens();
        userDAO.deleteAllUsers();
        gameDAO.deleteAllGames();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDAO.class})
    void registerCreatesAuthToken(Class<? extends AuthDAO> daoClass) throws ResponseException {
        var user = new UserData("cosmo1", "RiseAndShout123", "cosmo1@byu.edu");
        userDAO.insertUser(user);

        var authToken = authDAO.generateAuthToken();
        var auth = new AuthData(authToken, user.username());
        authDAO.insertAuth(auth);

        var retrievedAuth = authDAO.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals(authToken, retrievedAuth.authToken());
        assertEquals(user.username(), retrievedAuth.username());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDAO.class})
    void loginGeneratesAuthToken(Class<? extends AuthDAO> daoClass) throws ResponseException {
        var user = new UserData("cosmo2", "HelloBYU12", "cosmo2@byu.edu");
        userDAO.insertUser(user);

        var authToken = authDAO.generateAuthToken();
        var auth = new AuthData(authToken, user.username());
        authDAO.insertAuth(auth);

        var retrievedAuth = authDAO.getAuth(authToken);
        assertNotNull(retrievedAuth);
        assertEquals(authToken, retrievedAuth.authToken());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDAO.class})
    void logoutDeletesAuthToken(Class<? extends AuthDAO> daoClass) throws ResponseException {
        var user = new UserData("cosmo3", "NoBYUGood", "cosmo3@byu.edu");
        userDAO.insertUser(user);

        var authToken = authDAO.generateAuthToken();
        var auth = new AuthData(authToken, user.username());
        authDAO.insertAuth(auth);
        authDAO.deleteAuth(authToken);

        var retrievedAuth = authDAO.getAuth(authToken);
        assertNull(retrievedAuth);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlAuthDAO.class})
    void deleteAllAuthTokensAlsoDeletesAuths(Class<? extends AuthDAO> daoClass) throws ResponseException {
        var user1 = new UserData("player1", "BYUIsAwesome", "player1@byu.edu");
        var user2 = new UserData("player2", "BYUIsAwesome", "player2@byu.edu");
        userDAO.insertUser(user1);
        userDAO.insertUser(user2);
        String authToken1 = authDAO.generateAuthToken();
        String authToken2 = authDAO.generateAuthToken();


        authDAO.insertAuth(new AuthData(authToken1, "player1"));
        authDAO.insertAuth(new AuthData(authToken2, "player2"));

        authDAO.deleteAllAuthTokens();

        assertNull(authDAO.getAuth(authToken1));
        assertNull(authDAO.getAuth(authToken2));
    }
}
