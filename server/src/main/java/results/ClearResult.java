package results;

public class ClearResult {
    private final boolean success;

    public ClearResult(boolean success, String message) {
        this.success = success;
    }

    public boolean isSuccess(){
        return success;

    }


}
