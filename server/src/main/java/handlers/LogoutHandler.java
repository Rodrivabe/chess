package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler  implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Object handle(Request req, Response res) {

        try {
            String authToken = req.headers("authorization");
            userService.logout(authToken);
            res.status(200);
            return "{}";

        } catch (ResponseException e) {
            res.status(e.StatusCode());

            return e.toJson();
        }
    }


}
