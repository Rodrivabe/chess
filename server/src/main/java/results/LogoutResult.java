package results;

public class LogoutResult {
    private final boolean success;

    public LogoutResult(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;

    }
}
