package handlers;

import com.google.gson.Gson;
import results.ClearResult;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler implements BaseHandler{
    private final ClearService clearService;
    private final Gson gson;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
        this.gson = new Gson();
    }

    public Object handleRequest(Request req, Response res) {
        ClearResult result = clearService.clearDatabase();

        if (result.isSuccess()) {
            res.status(200);
            return "{}";

        } else {
            res.status(500);
            res.type("application/json");
            return gson.toJson(result);
        }

    }


}
