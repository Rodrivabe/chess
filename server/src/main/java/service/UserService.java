package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.LogoutResult;
import results.RegisterResult;

import java.util.Collection;
import java.util.Objects;

public class UserService {
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
        try {
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


            RegisterResult result = new RegisterResult(newUser.username(), newAuth.authToken());

            return result;
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: "+ e.getMessage());
        }

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


    public void logout(LogoutRequest logoutRequest) throws ResponseException {

        try{

            {


    }



/**
    public LoginResult login(LoginRequest loginRequest) {
    }

    public void logout(LogoutRequest logoutRequest) {
    }

 **/



    public UserData insertUser(UserData user) throws DataAccessException {
        return userDAO.insertUser(user);
    }


    public Collection<UserData> listUsers() throws DataAccessException {
        return userDAO.listUsers();
    }

    public void deleteAllUsers() throws ResponseException {
        userDAO.deleteAllUsers();
    }


}
