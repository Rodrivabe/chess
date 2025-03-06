package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData insertUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());

        users.put(user.username(), user);
        return user;
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void updateUser(String username, UserData updatedUser) {
        users.put(username, updatedUser);

    }

    public void deleteUser(String username) {
        users.remove(username);
    }

    public void deleteAll() {
        users.clear();
    }
}
