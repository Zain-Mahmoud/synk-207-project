package use_case.sync_to_google_calendar;

/**
 * The SyncToGoogleCalendarInputData class encapsulates the input data
 * required to initiate the synchronization process with Google Calendar.
 * Specifically, it stores the unique identifier for the user initiating
 * the synchronization.
 * <p>
 * This class is used as a data transfer object (DTO) and is passed to the
 * appropriate use case interactor to execute the synchronization logic.
 */


public class SyncToGoogleCalendarInputData {

    private final String userID;

    public SyncToGoogleCalendarInputData(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

}


