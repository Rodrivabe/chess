package handlers;

import exception.ResponseException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class ClearHandler {

    LoginRequest request = (LoginRequest)gson.fromJson(reqData, LoginRequest.class);

    LoginService service = new LoginService();
    LoginResult result = service.login(request);

    return gson.toJson(result);



    private Object deleteAllUsers(Request req, Response res) throws ResponseException {
        userService.deleteAllUsers();
        res.status(204);
        return "";
    }

}
