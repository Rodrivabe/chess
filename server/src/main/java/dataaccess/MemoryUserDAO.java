package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public void insertUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());

        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public Collection<UserData> listUsers() {
        return users.values();
    }

    public void deleteAllUsers() {
        users.clear();
    }
}
