package service;
import dataaccess.DataAccessException;

public class ClearService {

    public void ClearDatabase() throws DataAccessException {
        DataAccessObject dao = new DataAccessObject();
        dao.clear();

    }


}
