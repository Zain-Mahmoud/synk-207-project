package interface_adapter.sync_to_google_calendar;

import use_case.sync_to_google_calendar.SyncToGoogleCalendarOutputBoundary;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarOutputData;

public class SyncToGoogleCalendarPresenter implements SyncToGoogleCalendarOutputBoundary {
    private final SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel; // TODO: Hold view model used to notify the UI about sync results

    public SyncToGoogleCalendarPresenter(SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel) { // TODO: Presenter now only depends on its view model
        this.syncToGoogleCalendarViewModel = syncToGoogleCalendarViewModel;
    }


    /**
     * @param outputData the explanation of the Success
     */
    @Override
    public void prepareSuccessView(SyncToGoogleCalendarOutputData outputData) {
        SyncToGoogleCalendarControllerState state = syncToGoogleCalendarViewModel.getState(); // TODO: Pull mutable state to update success info
        state.setStatusMessage(outputData.getResult()); // TODO: Surface success message returned from interactor
        state.setError(""); // TODO: Clear any previous error so success shows cleanly
        state.setSuccess(outputData.isSuccess()); // TODO: Mark last sync based on output data flag
        syncToGoogleCalendarViewModel.setState(state); // TODO: Persist updated state back to the view model
        syncToGoogleCalendarViewModel.firePropertyChanged("sync"); // TODO: Notify listeners that a sync event occurred
    }

    /**
     * @param errorMessage the explanation of the failure
     */
    @Override
    public void prepareFailView(String errorMessage) {

        SyncToGoogleCalendarControllerState state = syncToGoogleCalendarViewModel.getState(); // TODO: Pull state to record failure
        state.setError(errorMessage); // TODO: Store error for UI display
        state.setStatusMessage(""); // TODO: Clear any stale success message when a failure occurs
        state.setSuccess(false); // TODO: Flag failed sync
        syncToGoogleCalendarViewModel.setState(state); // TODO: Persist failure state to view model
        syncToGoogleCalendarViewModel.firePropertyChanged("sync"); // TODO: Notify listeners about sync failure

    }
}
