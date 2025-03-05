package handlers;

import exception.ResponseException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class ClearHandler {


    private Object deleteAllUsers(Request req, Response res) throws ResponseException {
        UserService userService = new UserService();
        userService.deleteAllUsers();
        res.status(204);
        return "";
    }

}
