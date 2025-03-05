package service;

import dataaccess.UserDAO;
import exception.ResponseException;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    public void deleteAllUsers() throws ResponseException {
        userDAO.deleteAllUsers();
    }

}
