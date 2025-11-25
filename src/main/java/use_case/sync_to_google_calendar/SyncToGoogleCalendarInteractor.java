package use_case.sync_to_google_calendar;

import entities.Completable;
import entities.Task;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarPresenter;
import use_case.gateways.CalendarGateway;
import use_case.gateways.TaskGateway;
import use_case.gateways.UserGateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SyncToGoogleCalendarInteractor implements SyncToGoogleCalendarInputBoundary {

    /**
     * The main bread and butter, Has all the functionality for the use case.
     * Need to Do something with DAI and then Package to output data that and send it to the
     * Output boundary, that the presenter implements
     */

    private final TaskGateway taskGateway;
    private final CalendarGateway calendarGateway;
    private final SyncToGoogleCalendarOutputBoundary outputBoundary;

    public SyncToGoogleCalendarInteractor(TaskGateway taskGateway, CalendarGateway calendarGateway,
                                          SyncToGoogleCalendarOutputBoundary outputBoundary) {
        this.taskGateway = taskGateway;
        this.calendarGateway = calendarGateway;
        this.outputBoundary = outputBoundary;
    }

    /**
     * Get user events, sync them to Google Calendar. with a method call
     *
     */

    @Override
    public void execute(SyncToGoogleCalendarInputData inputData) {
        try {
            // TODO Turn into Completable
            // get tasks and sync
            String userId = inputData.getUserID();
            if (!calendarGateway.hasStoredCredential(userId)) { // run auth if credential is missing
                calendarGateway.authenticateUser(userId); // initiate OAuth flow for user
            }
            ArrayList<Task> userCompletables = taskGateway.fetchTasks(userId);
            // Prepare output data
            for(Completable completable : userCompletables) {
                calendarGateway.createEvent(userId, completable);
            }

            SyncToGoogleCalendarOutputData outputData = new SyncToGoogleCalendarOutputData(true, "Synced " + userCompletables.size() + " items!");

            // Call output boundary to pass to Presenter
            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            outputBoundary.prepareFailView("An error occurred during synchronization: " + e.getMessage());
        }
    }

    // --------------HELPERS----------------

}

