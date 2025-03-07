package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import results.LoginResult;

import java.util.Collection;
import java.util.Objects;

public class AuthService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public AuthService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public Collection<AuthData> listAuths() throws DataAccessException {
        return authDAO.listAuths();
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {


        if (loginRequest.username() == null || loginRequest.username().isBlank() ||
                loginRequest.password() == null || loginRequest.password().isBlank()) {
            throw new ResponseException(400, "Error: bad request");

        }
        try {
            UserData user = userDAO.getUser(loginRequest.username());
            if (user == null || !Objects.equals(user.password(), loginRequest.password())) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            String authToken = authDAO.generateAuthToken();
            AuthData newAuth = new AuthData(authToken, user.username());
            authDAO.insertAuth(newAuth);

            LoginResult result = new LoginResult(user.username(), newAuth.authToken());

            return result;
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: "+ e.getMessage());
        }

    }

}
