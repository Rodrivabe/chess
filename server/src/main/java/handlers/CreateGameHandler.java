package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import requests.CreateGameRequest;
import results.CreateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }


    @Override
    public Object handle(Request req, Response res) {
        CreateGameRequest request = gson.fromJson(req.body(), CreateGameRequest.class);

        try {
            String authToken = req.headers("authorization");
            CreateGameResult result = gameService.createGame(request, authToken);
            res.status(200);
            return gson.toJson(result);

        } catch (ResponseException e) {
            res.status(e.StatusCode());

            return e.toJson();
        }

    }
}