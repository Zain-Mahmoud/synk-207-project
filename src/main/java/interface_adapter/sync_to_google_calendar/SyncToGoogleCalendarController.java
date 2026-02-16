package interface_adapter.sync_to_google_calendar;

import use_case.sync_to_google_calendar.SyncToGoogleCalendarInputBoundary;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarInputData;

public class SyncToGoogleCalendarController {
   /**
    We are calling a method in the input Boundary
    First we package the data from the View then call the input Boundary implemented by the use
    Case interactor
    */

    private final SyncToGoogleCalendarInputBoundary syncToGoogleCalendarUseCaseInteractor;

    public SyncToGoogleCalendarController(SyncToGoogleCalendarInputBoundary syncToGoogleCalendarUseCaseInteractor) {
        this.syncToGoogleCalendarUseCaseInteractor = syncToGoogleCalendarUseCaseInteractor;
    }

    /**
     * Executes the sync to GCal Use Case.
     *
     * @param userId press true?
     *
     */
    // Here we construct a ID object
    public void execute(String userId) {
        // EXECUTE ASYNC to avoid blocking the JavaFX Application Thread
        new Thread(() -> {
            try {
                final SyncToGoogleCalendarInputData syncToGoogleCalendarInputData =
                        new SyncToGoogleCalendarInputData(userId);
                syncToGoogleCalendarUseCaseInteractor.execute(syncToGoogleCalendarInputData);
            } catch (Exception e) {
                // If any unexpected error occurs here, ensure it doesn't crash the thread silently
                e.printStackTrace();
            }
        }).start();
    }


}
