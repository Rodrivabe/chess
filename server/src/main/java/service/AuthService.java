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



}
