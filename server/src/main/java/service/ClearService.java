package service;
import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;

public class ClearService {

    public void ClearDatabase() throws DataAccessException {
        DataAccessObject dao = new DataAccessObject();
        dao.clear();

    }


}
