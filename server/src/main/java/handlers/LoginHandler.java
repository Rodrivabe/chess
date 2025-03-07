package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import requests.RegisterRequest;
import results.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler extends HandlerBase implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Object handle(Request req, Response res) {
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResult result = userService.register(request);
            res.status(200);
            return gson.toJson(result);

        } catch (ResponseException e) {
            res.status(e.StatusCode());

            return e.toJson();
        }

    }

}