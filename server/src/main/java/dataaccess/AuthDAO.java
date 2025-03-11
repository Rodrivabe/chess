package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void insertAuth(AuthData authdata);

    AuthData getAuth(String authToken);


    String generateAuthToken();


    void deleteAuth(String authToken);

    void deleteAllAuthTokens();

}
