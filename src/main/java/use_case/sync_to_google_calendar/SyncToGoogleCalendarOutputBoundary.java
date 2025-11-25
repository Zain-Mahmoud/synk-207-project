package use_case.sync_to_google_calendar;

public interface SyncToGoogleCalendarOutputBoundary {


    /**
     * Prepares the failure view for the Fail to Sync to Google Calendar Use Case.
     * @param outputData the explanation of the Success
     */

    void prepareSuccessView(SyncToGoogleCalendarOutputData outputData);

    /**
     * Prepares the failure view for the Fail to Sync to Google Calendar Use Case.
     * @param errorMessage the explanation of the failure
     */

    void prepareFailView(String errorMessage);
    }

