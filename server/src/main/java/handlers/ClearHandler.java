package handlers;

import com.google.gson.Gson;
import results.ClearResult;
import service.ClearService;
import spark.Response;

public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
        this.gson = new Gson();
    }

    public Object handleRequest(Response res) {
        ClearResult result = clearService.clearDatabase();

        if (result.success()) {
            res.status(200);
            return "{}";

        } else {
            res.status(500);
            res.type("application/json");
            return gson.toJson(result);
        }

    }


}
