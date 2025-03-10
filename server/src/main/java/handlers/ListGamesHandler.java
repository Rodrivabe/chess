package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import results.ListGamesResult;
import results.LoginResult;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler  implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }


    @Override
    public Object handle(Request req, Response res) {

        try {
            String authToken = req.headers("authorization");
            ListGamesResult result = gameService.listGames(authToken);
            res.status(200);
            return gson.toJson(result);

        } catch (ResponseException e) {
            res.status(e.StatusCode());

            return e.toJson();
        }
    }


}
