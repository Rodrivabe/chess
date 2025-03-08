package handlers;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;

public class HandlerBase {

    public boolean verifyAuthToken(String authToken) throws DataAccessException {

        if(authToken == null || authToken.isBlank()){
            throw new DataAccessException("Error: unauthorized");
        }

        AuthDAO authDAO = new AuthDAO();
        AuthData authData;

        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }

        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

    }




}
