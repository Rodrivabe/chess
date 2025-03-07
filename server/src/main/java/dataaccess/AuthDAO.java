package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {

    AuthData insertAuth(AuthData authdata) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    Collection<AuthData> listAuths() throws DataAccessException;


    public String generateAuthToken() throws DataAccessException;


    void deleteAuth(AuthData authData) throws DataAccessException;

    void deleteAllAuthTokens() throws DataAccessException;

}
