package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.UserData;
import results.LoginResult;
import results.RegisterResult;

import java.util.Collection;

public class UserService{
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) {

    }


    public LoginResult login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}

    public UserData insertUser (UserData user) throws DataAccessException {
        return userDAO.insertUser(user);
    }



    public Collection<UserData> listUsers() throws DataAccessException {
        return userDAO.listUsers();
    }

    public void deleteAllUsers() throws ResponseException {
        userDAO.deleteAllUsers();
    }


}
