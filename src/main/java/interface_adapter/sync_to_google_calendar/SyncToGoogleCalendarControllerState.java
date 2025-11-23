package interface_adapter.sync_to_google_calendar;

public class SyncToGoogleCalendarControllerState {
    private String error = "Error with Syncing";
    private String statusMessage = "";
    private boolean success;

    public void setError(String errorMessage) {
        this.error = errorMessage;
    }

    public String getError() {
        return error;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean isSuccess() { //
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
