package use_case.sync_to_google_calendar;

/**
 * Defines the input boundary for the Sync to Google Calendar use case.
 * This interface abstracts the process of initiating the synchronization
 * of user data to Google Calendar.
 * <p>
 * The implementing class in G-cal Link should contain the logic to handle the
 * synchronization process, using the provided input data.
 *
 */
public interface SyncToGoogleCalendarInputBoundary {
    void execute(SyncToGoogleCalendarInputData inputData);
}

