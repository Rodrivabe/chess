package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import requests.LoginRequest;
import requests.LogoutRequest;
import results.LoginResult;
import results.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler extends HandlerBase implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Object handle(Request req, Response res) {
        LogoutRequest request = gson.fromJson(req.body(), LogoutRequest.class);
        try {
            String authToken = req.headers("authorization");
            verifyAuthToken(authToken);
            userService.logout(request);
            res.status(200);
            return gson.toJson(result);

        } catch (ResponseException e) {
            res.status(e.StatusCode());

            return e.toJson();
        }
    }


}
