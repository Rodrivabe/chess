package server;

import dataaccess.*;
import exception.ResponseException;
import handlers.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.Spark;

public class Server {
        private AuthDAO authDAO;
        private GameDAO gameDAO;
        private UserDAO userDAO;

    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        try {
            userDAO = new MySqlUserDAO();
            gameDAO = new MySqlGameDAO();
            authDAO = new MySqlAuthDAO();
        } catch (ResponseException e) {
            System.out.println("Error with creating your DAO");
        }



        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
        UserService userService = new UserService(authDAO, userDAO);
        GameService gameService = new GameService(authDAO, gameDAO);

        // Register your endpoints and handle exceptions here.
        //clear
        Spark.delete("/db", (req, res) -> (new ClearHandler(clearService)).handleRequest(res));
        //Register
        Spark.post("/user", new RegisterHandler(userService));
        //Login
        Spark.post("/session", new LoginHandler(userService));
        //Logout
        Spark.delete("/session", new LogoutHandler(userService));
        //List Games
        Spark.get("/game", new ListGamesHandler(gameService));
        //Create Game
        Spark.post("/game", new CreateGameHandler(gameService));
        //Join Game
        Spark.put("/game", new JoinGameHandler(gameService));



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
