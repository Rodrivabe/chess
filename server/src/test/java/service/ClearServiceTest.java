package service;

import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;

public class ClearServiceTest {
    static final ClearService service = new ClearService(new MemoryDataAccess());

    @BeforeEach
    void clear() throws ResponseException {
        clear.deleteAllPets();
}
