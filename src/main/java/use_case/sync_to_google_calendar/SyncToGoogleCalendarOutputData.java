package use_case.sync_to_google_calendar;

public class SyncToGoogleCalendarOutputData {
     private final boolean success;
     private final String result;

    public SyncToGoogleCalendarOutputData(boolean success, String result) {
        this.success = success;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }
}

