package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import handlers.HandlerBase;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;

import java.util.Collection;
import java.util.Objects;

public class UserService extends HandlerBase {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {


        if (registerRequest.username() == null || registerRequest.username().isBlank() ||
                registerRequest.password() == null || registerRequest.password().isBlank() ||
                registerRequest.email() == null || registerRequest.email().isBlank()) {
            throw new ResponseException(400, "Error: bad request");

        }
        UserData user = userDAO.getUser(registerRequest.username());
        if (user != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(),
                registerRequest.email());

        userDAO.insertUser(newUser);
        String authToken = authDAO.generateAuthToken();
        AuthData newAuth = new AuthData(authToken, newUser.username());
        authDAO.insertAuth(newAuth);


        return new RegisterResult(newUser.username(), newAuth.authToken());

    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {


        if (loginRequest.username() == null || loginRequest.username().isBlank() ||
                loginRequest.password() == null || loginRequest.password().isBlank()) {
            throw new ResponseException(400, "Error: bad request");

        }
        UserData user = userDAO.getUser(loginRequest.username());
        if (user == null || !Objects.equals(user.password(), loginRequest.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        String authToken = authDAO.generateAuthToken();
        AuthData newAuth = new AuthData(authToken, user.username());
        authDAO.insertAuth(newAuth);

        return new LoginResult(user.username(), newAuth.authToken());

    }


    public void logout(String authToken) throws ResponseException {

            verifyAuthToken(authDAO, authToken);
            authDAO.deleteAuth(authToken);
    }



/**
    public LoginResult login(LoginRequest loginRequest) {
    }

    public void logout(LogoutRequest logoutRequest) {
    }

 **/


    public Collection<UserData> listAllUsers() {
        return userDAO.listUsers();
    }


}
