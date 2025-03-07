package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.security.LoginService;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;

import java.util.Collection;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public Collection<AuthData> listAuths() throws DataAccessException {
        return authDAO.listAuths();
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {


        if (loginRequest.username() == null || loginRequest.username().isBlank() ||
                loginRequest.password() == null || loginRequest.password().isBlank() ||
                loginRequest.email() == null || loginRequest.email().isBlank()) {
            throw new ResponseException(400, "Error: bad request");

        }
        try {
            UserData user = userDAO.getUser(loginRequest.username());
            if (user != null) {
                throw new ResponseException(403, "Error: already taken");
            }
            UserData newUser = new UserData(loginRequest.username(), loginRequest.password(),
                    loginRequest.email());

            userDAO.insertUser(newUser);
            String authToken = authDAO.generateAuthToken();
            AuthData newAuth = new AuthData(authToken, newUser.username());
            authDAO.insertAuth(newAuth);


            LoginResult result = new LoginResult(newUser.username(), newAuth.authToken());

            return result;
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: "+ e.getMessage());
        }

    }

}
