package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import results.ClearResult;

public class ClearService {
    private final DataAccessObject dao;

    public ClearService(DataAccessObject dao) {
        this.dao = dao;
    }

    public void clear() throws DataAccessException {
        dao.clearAll(); // Calls the DAO's method to clear all data
    }
}
