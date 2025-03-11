package dataaccess;

import model.UserData;

import java.util.Collection;

public interface UserDAO {

    void insertUser(UserData user);

    UserData getUser(String username);

    Collection<UserData> listUsers();

    void deleteAllUsers();

}
