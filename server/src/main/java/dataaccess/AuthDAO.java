package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.util.Collection;

public interface AuthDAO {

    void insertAuth(AuthData authdata) throws DataAccessException;

    AuthData getAuth(String authToken) throws ResponseException;

    Collection<AuthData> listAuths() throws DataAccessException;


    public String generateAuthToken() throws DataAccessException;


    void deleteAuth(String authToken) throws ResponseException;

    void deleteAllAuthTokens() throws DataAccessException;

}
