package interface_adapter.sync_to_google_calendar;

import use_case.sync_to_google_calendar.SyncToGoogleCalendarOutputBoundary;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarOutputData;

public class SyncToGoogleCalendarPresenter implements SyncToGoogleCalendarOutputBoundary {
    private final SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel; //  Hold view model used to notify the UI about sync results

    public SyncToGoogleCalendarPresenter(SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel) { //  Presenter now only depends on its view model
        this.syncToGoogleCalendarViewModel = syncToGoogleCalendarViewModel;
    }


    /**
     * @param outputData the explanation of the Success
     */
    @Override
    public void prepareSuccessView(SyncToGoogleCalendarOutputData outputData) {
        SyncToGoogleCalendarControllerState state = syncToGoogleCalendarViewModel.getState(); // TPull mutable state to update success info
        state.setStatusMessage(outputData.getResult()); //  Surface success message returned from interactor
        state.setError(""); //  Clear any previous error so success shows cleanly
        state.setSuccess(outputData.isSuccess()); //  Mark last sync based on output data flag
        syncToGoogleCalendarViewModel.setState(state); //  Persist updated state back to the view model
        syncToGoogleCalendarViewModel.firePropertyChanged("sync"); //  Notify listeners that a sync event occurred
    }

    /**
     * @param errorMessage the explanation of the failure
     */
    @Override
    public void prepareFailView(String errorMessage) {

        SyncToGoogleCalendarControllerState state = syncToGoogleCalendarViewModel.getState(); //  Pull state to record failure
        state.setError(errorMessage); //  Store error for UI display
        state.setStatusMessage(""); //  Clear any stale success message when a failure occurs
        state.setSuccess(false); //  Flag failed sync
        syncToGoogleCalendarViewModel.setState(state); //  Persist failure state to view model
        syncToGoogleCalendarViewModel.firePropertyChanged("sync"); //  Notify listeners about sync failure

    }
}
