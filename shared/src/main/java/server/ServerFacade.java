package server;

import com.google.gson.Gson;
import exception.ResponseException;
import requests.CreateGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.LoginResult;
import results.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(int port){
        this.serverUrl = "http://localhost:" + port;

    }

    public RegisterResult register(RegisterRequest request) throws ResponseException{
        var path = "/user";
        return this.makeRequest("POST", path, request, null, RegisterResult.class);

    }

    public LoginResult login(LoginRequest request) throws ResponseException{
        var path = "/session";
        return this.makeRequest("POST", path, request, null, LoginResult.class);
    }



    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, authToken, null);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws ResponseException{
        var path = "/game";
        return this.makeRequest("POST", path, request, authToken, CreateGameResult.class);
    }

    public void clearDataBase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null,null, null);
    }



    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);


            setHeaders(http, authToken);
            if(request != null){
                writeRequestBody(http, request);
            }


            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void setHeaders(HttpURLConnection http, String authToken) {
        if (authToken != null) {
            http.setRequestProperty("Authorization", authToken);
        } else {
            http.setRequestProperty("Content-Type", "application/json");
        }
    }


    private static void writeRequestBody(HttpURLConnection http, Object request) throws IOException {
        String reqData = new Gson().toJson(request);
        try (OutputStream reqBody = http.getOutputStream()) {
            reqBody.write(reqData.getBytes());
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
