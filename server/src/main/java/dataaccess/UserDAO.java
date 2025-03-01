package dataaccess;

import model.GameData;
import model.UserData;

public interface UserDAO {

    UserData insertUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;

    public void updateUser(String username, UserData updatedUser);

    void deleteAllUsers();

}
