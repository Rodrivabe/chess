package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    public void insertAuth(AuthData authdata){
        authTokens.put(authdata.authToken(), authdata);
    }


    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    public String generateAuthToken() {

        return UUID.randomUUID().toString();
    }


    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    public void deleteAllAuthTokens() {
        authTokens.clear();
    }


}
