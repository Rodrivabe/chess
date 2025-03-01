package dataaccess;

import model.UserData;

public interface UserDAO {

    UserData insertUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;

    void deleteAllUsers();

}
