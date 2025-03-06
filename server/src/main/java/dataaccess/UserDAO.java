package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;

public interface UserDAO {

    UserData insertUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;

    void updateUser(String username, UserData updatedUser);

    Collection<UserData> listUsers() throws DataAccessException;

    void deleteAllUsers();

}
