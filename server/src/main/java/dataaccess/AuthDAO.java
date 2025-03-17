package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface AuthDAO {

    void insertAuth(AuthData authdata) throws ResponseException;;

    AuthData getAuth(String authToken) throws ResponseException;;


    String generateAuthToken() throws ResponseException;;


    void deleteAuth(String authToken) throws ResponseException;;

    void deleteAllAuthTokens() throws ResponseException;;

}
