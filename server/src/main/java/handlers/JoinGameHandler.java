package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import requests.JoinGameRequest;
import requests.LoginRequest;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler  implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }


    @Override
    public Object handle(Request req, Response res) {
        JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);

        try {
            String authToken = req.headers("authorization");
            gameService.joinGame(request, authToken);
            res.status(200);
            return "{}";

        } catch (ResponseException e) {
            res.status(e.StatusCode());

            return e.toJson();
        }
    }


}
