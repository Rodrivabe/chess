package handlers;

import com.google.gson.Gson;
import requests.RegisterRequest;
import results.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.imageio.spi.RegisterableService;

public class RegisterHandler extends HandlerBase implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Object handle(Request req, Response res) {
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);

        //If user i

    }

    public

}


/**
 * LoginRequest request = (LoginRequest)gson.fromJson(reqData, LoginRequest.class);
 *
 * LoginService service = new LoginService();
 * LoginResult result = service.login(request);
 *
 * return gson.toJson(result);
 */