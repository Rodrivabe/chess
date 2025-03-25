package server;

import com.google.gson.Gson;
import exception.ResponseException;
import requests.LoginRequest;
import requests.RegisterRequest;
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
        return this.makeRequest("POST", path, request, RegisterResult.class, false);

    }

    public LoginResult login(LoginRequest request) throws ResponseException{
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class, false);
    }



    public void logout(String authToken) throws ResponseException {

        var path = "/session";
        this.makeRequest("DELETE", path, authToken, LoginResult.class, true);


    }

    public void create

    public void clearDataBase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, false);
    }



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass,
                              boolean authorization) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            String authToken = null;
            if (authorization && request != null) {
                authToken = request.toString();
            }
            setHeaders(http, authorization, authToken);

            if (!authorization && request != null) {
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

    private static void setHeaders(HttpURLConnection http, boolean authorization, String authToken) {
        if (authorization && authToken != null) {
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
