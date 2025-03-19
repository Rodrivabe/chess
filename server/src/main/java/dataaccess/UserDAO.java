package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.util.Collection;

public interface UserDAO {

    void insertUser(UserData user) throws ResponseException;

    UserData getUser(String username) throws ResponseException;

    Collection<UserData> listUsers() throws ResponseException;

    void deleteAllUsers() throws ResponseException;

}
