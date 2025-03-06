package server;
import dataaccess.*;
import handlers.ClearHandler;
import service.ClearService;
import service.UserService;
import spark.*;

public class Server {


    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        AuthDAO AuthData = new MemoryAuthDAO();
        GameDAO GameData = new MemoryGameDAO();
        UserDAO UserData = new MemoryUserDAO();


        ClearService clearService = new ClearService(AuthData, UserData, GameData);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> (new ClearHandler(clearService)).handleRequest(req, res));

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
