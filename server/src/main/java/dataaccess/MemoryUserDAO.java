package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public int insertUser(UserData user) {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        user = new UserData(user.username(), hashedPassword, user.email());
        users.put(user.username(), user);
        return 0;
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
