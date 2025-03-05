package server;
import dataaccess.*;
import handlers.ClearHandler;
import service.UserService;
import spark.*;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final AuthService authService;

    public Server(UserService userService) {
        this.userService = userService;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        AuthDAO AuthData = new MemoryAuthDAO();
        GameDAO GameData = new MemoryGameDAO();
        UserDAO UserData = new MemoryUserDAO();

        // Register your endpoints and handle exceptions here.
        Spark.delete("/pet", this::deleteAllPets);
        Spark.delete("/user", (req, res) -> (new ClearHandler()).handleRequest(req, res));

        //This line initializes the server and can be removed once you have a functioning endpoint 

        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
