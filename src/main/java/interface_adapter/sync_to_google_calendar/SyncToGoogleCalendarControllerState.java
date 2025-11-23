package interface_adapter.sync_to_google_calendar;

public class SyncToGoogleCalendarControllerState {
    private String error = "Error with Syncing"; // TODO: Default sync error message holder
    private String statusMessage = ""; // TODO: Track latest sync success message
    private boolean success; // TODO: Track if last sync attempt was successful

    public void setError(String errorMessage) {
        this.error = errorMessage;
    }

    public String getError() {
        return error;
    }

    public void setStatusMessage(String statusMessage) { // TODO: Store sync success message
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() { // TODO: Expose sync success message
        return statusMessage;
    }

    public boolean isSuccess() { // TODO: Expose sync success state
        return success;
    }

    public void setSuccess(boolean success) { // TODO: Record sync success state
        this.success = success;
    }
}
