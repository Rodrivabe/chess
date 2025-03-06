package dataaccess;

import model.AuthData;
import model.UserData;


import java.util.Collection;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    public AuthData insertAuth(AuthData authdata) {
        authTokens.put(authdata.authToken(), authdata);
        return authdata;
    }


    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    public Collection<AuthData> listAuths(){
        return authTokens.values();
    }


    public void deleteAuth(AuthData authData) throws DataAccessException {
        authTokens.remove(authData.authToken());
    }

    public void deleteAllAuthTokens() {
        authTokens.clear();
    }
}
