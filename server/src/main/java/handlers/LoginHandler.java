package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import requests.LoginRequest;
 import results.LoginResult;
import service.AuthService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler extends HandlerBase implements Route {
    private final AuthService authService;
    private final Gson gson = new Gson();

    public LoginHandler(AuthService authService) {
        this.authService = authService;
    }


    @Override
    public Object handle(Request req, Response res) {
        LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
        try {
            LoginResult result = authService.login(request);
            res.status(200);
            return gson.toJson(result);

        } catch (ResponseException e) {
            res.status(e.StatusCode());

            return e.toJson();
        }

    }

}