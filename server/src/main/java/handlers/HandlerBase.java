package handlers;

import dataaccess.AuthDAO;
import exception.ResponseException;
import model.AuthData;


public class HandlerBase {

    public void verifyAuthToken(AuthDAO authDAO, String authToken) throws ResponseException {

        if(authToken == null || authToken.isBlank() || authDAO.getAuth(authToken) == null){
            throw new ResponseException(401, "Error: unauthorized");
        }

    }




}
