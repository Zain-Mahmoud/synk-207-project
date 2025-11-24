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
        final SyncToGoogleCalendarInputData syncToGoogleCalendarInputData = new SyncToGoogleCalendarInputData(userId);
        syncToGoogleCalendarUseCaseInteractor.execute(syncToGoogleCalendarInputData); // Trigger sync interactor with packaged input
    }


}
