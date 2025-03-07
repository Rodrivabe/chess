package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import requests.RegisterRequest;
import results.RegisterResult;

import java.util.Collection;

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
