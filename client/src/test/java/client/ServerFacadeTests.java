package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.ListGamesResult;
import results.LoginResult;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);

    }


    @BeforeEach
    void clear() {
        assertDoesNotThrow(() -> facade.clearDataBase());
    }

    @Test
    void registerPositive() throws ResponseException {
        RegisterRequest request = new RegisterRequest("cosmo1",
                "pass123", "cosmo1@example.com");
        var result = facade.register(request);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("cosmo1", result.username());
    }

    @Test
    void registerNegativeExistingUser() throws ResponseException {
        RegisterRequest request = new RegisterRequest("cosmo1", "pass123", "cosmo1@example.com");
        facade.register(request); // First registration works

        // Try to register same user again
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register(request);
        });
    }

    @Test
    void loginPositive() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("cosmo1",
                "pass123", "cosmo1@example.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("cosmo1",
                "pass123");
        var result = facade.login(loginRequest);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("cosmo1", result.username());
        Assertions.assertTrue(result.authToken().length() > 5);
    }

    @Test
    void loginNegativeWrongPassword() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("cosmo1", "pass123", "cosmo1@example.com");
        facade.register(registerRequest);

        LoginRequest wrongPasswordRequest = new LoginRequest("cosmo1", "wrongpass");

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login(wrongPasswordRequest);
        });
    }

    @Test
    void logoutPositive() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("cosmo1",
                "pass123", "cosmo1@example.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("cosmo1",
                "pass123");
        var loginResult = facade.login(loginRequest);
        assertDoesNotThrow(() -> facade.logout(loginResult.authToken()));

    }

    @Test
    void loginNegativeUserDoesNotExist() {
        LoginRequest request = new LoginRequest("nonexistent", "password");
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login(request);
        });
    }

    @Test
    void createGamePositive() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("cosmo1", "pass123", "cosmo1@example.com");
        facade.register(registerRequest);
        LoginResult loginResult = facade.login(new LoginRequest("cosmo1", "pass123"));
        String authToken = loginResult.authToken();

        CreateGameRequest createGameRequest = new CreateGameRequest("My Game");
        CreateGameResult result = facade.createGame(createGameRequest, authToken);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.gameID());
    }
    @Test
    void createGameNegative() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("cosmo1", "pass123", "cosmo1@example.com");
        facade.register(registerRequest);
        LoginResult loginResult = facade.login(new LoginRequest("cosmo1", "pass123"));
        String authToken = loginResult.authToken();

        CreateGameRequest badRequest = new CreateGameRequest("");

        ResponseException exception = Assertions.assertThrows(ResponseException.class, () -> {
            facade.createGame(badRequest, authToken);
        });

        Assertions.assertEquals(400, exception.statusCode());
    }

    @Test
    void listGamesPositive() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("cosmo1", "pass123", "cosmo1@example.com");
        facade.register(registerRequest);
        LoginResult loginResult = facade.login(new LoginRequest("cosmo1", "pass123"));
        String authToken = loginResult.authToken();

        facade.createGame(new CreateGameRequest("Test Game 1"), authToken);
        facade.createGame(new CreateGameRequest("Test Game 2"), authToken);


        ListGamesResult result = facade.listGames(authToken);

        // Step 4: Assertions
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.games());
        Assertions.assertEquals(2, result.games().size());

        boolean containsCreatedGame = result.games().stream()
                .anyMatch(game -> "Test Game 1".equals(game.gameName()));
        Assertions.assertTrue(containsCreatedGame);
    }

    @Test
    void listGamesNegative_invalidAuthToken() {
        String invalidToken = "not-a-valid-token";

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.listGames(invalidToken);
        });
    }





    @AfterAll
    static void stopServer() {
        server.stop();
    }



    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
