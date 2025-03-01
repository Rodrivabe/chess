package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData insertAuth(AuthData authdata) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;



    void deleteAuth(AuthData authData) throws DataAccessException;

}
