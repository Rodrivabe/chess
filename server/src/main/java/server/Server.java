package server;

import dataaccess.*;
import handlers.ClearHandler;
import handlers.LoginHandler;
import handlers.RegisterHandler;
import service.AuthService;
import service.ClearService;
import service.UserService;
import spark.Spark;

public class Server {


    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();


        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
        UserService userService = new UserService(authDAO, userDAO);
        AuthService authService = new AuthService(authDAO, userDAO);

        // Register your endpoints and handle exceptions here.
        //delete
        Spark.delete("/db", (req, res) -> (new ClearHandler(clearService)).handleRequest(req, res));
        //Register
        Spark.post("/user", new RegisterHandler(userService));
        //Login
        Spark.post("/session", new LoginHandler(authService));


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
