package interface_adapter.sync_to_google_calendar;

import interface_adapter.ViewModel;

public class SyncToGoogleCalendarViewModel extends ViewModel<SyncToGoogleCalendarControllerState> {
    public SyncToGoogleCalendarViewModel() {
        super("Synced to Google Calendar");
        setState(new SyncToGoogleCalendarControllerState());
    }
}
