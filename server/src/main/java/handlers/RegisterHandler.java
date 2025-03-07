package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import requests.RegisterRequest;
import results.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler extends HandlerBase implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Object handle(Request req, Response res) {
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResult result = userService.register(request);
            return result;
        } catch (ResponseException e) {
            return new ResponseException(e.StatusCode(), e.getMessage());
        }

        //If user i

    }

    public
}


/**
 * LoginRequest request = (LoginRequest)gson.fromJson(reqData, LoginRequest.class);
 * <p>
 * LoginService service = new LoginService();
 * LoginResult result = service.login(request);
 * <p>
 * return gson.toJson(result);
 */