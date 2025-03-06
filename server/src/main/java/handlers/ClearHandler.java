package handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import results.ClearResult;
import service.ClearService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
        this.gson = new Gson();
    }

    @Override
    public Object handleRequest(Request req, Response res) {
        ClearResult result = clearService.clearDatabase();

        if (result.isSuccess()) {
            res.status(200);
        } else {
            res.status(500);
        }

        res.type("application/json");
        return gson.toJson(result);
    }


}
