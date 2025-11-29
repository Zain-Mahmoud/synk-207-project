package use_case;

import com.google.api.services.calendar.model.Event;
import entities.Task;
import entities.TaskBuilder;
import org.junit.jupiter.api.Test;
import use_case.gateways.CalendarGateway;
import use_case.gateways.TaskGateway;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarInputData;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarInteractor;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarOutputBoundary;
import use_case.sync_to_google_calendar.SyncToGoogleCalendarOutputData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for {@link use_case.sync_to_google_calendar.SyncToGoogleCalendarInteractor}.
 * See @ link for more details of the tested class
 */



public class SyncToGoogleCalendarInteractorTest {

    /**
     * Verifies task sync when a credential already exists and no authentication is needed.
     */
    @Test
    void syncsWithExistingCredential() {
        FakeTaskGateway taskGateway = new FakeTaskGateway();
        taskGateway.addForUser("user-123", buildTask("Task 1"));
        taskGateway.addForUser("user-123", buildTask("Task 2"));

        FakeCalendarGateway calendarGateway = new FakeCalendarGateway();
        calendarGateway.storedCredential = true;
        CapturingOutputBoundary outputBoundary = new CapturingOutputBoundary();

        SyncToGoogleCalendarInteractor interactor = new SyncToGoogleCalendarInteractor(
                taskGateway, calendarGateway, outputBoundary);

        interactor.execute(new SyncToGoogleCalendarInputData("user-123"));

        assertFalse(calendarGateway.authenticated, "Should not authenticate when credential exists");
        assertEquals(2, calendarGateway.created.size(), "All tasks should be synced to calendar");
        assertNotNull(outputBoundary.successData);
        assertNull(outputBoundary.failureMessage);
        assertTrue(outputBoundary.successData.isSuccess());
        assertEquals("Synced 2 items!", outputBoundary.successData.getResult());
    }

    /**
     * Ensures the interactor triggers authentication if no stored credential exists.
     */
    @Test
    void authenticatesWhenCredentialMissing() {
        FakeTaskGateway taskGateway = new FakeTaskGateway();
        taskGateway.addForUser("user-abc", buildTask("Single Task"));

        FakeCalendarGateway calendarGateway = new FakeCalendarGateway();
        calendarGateway.storedCredential = false;
        CapturingOutputBoundary outputBoundary = new CapturingOutputBoundary();

        SyncToGoogleCalendarInteractor interactor = new SyncToGoogleCalendarInteractor(
                taskGateway, calendarGateway, outputBoundary);

        interactor.execute(new SyncToGoogleCalendarInputData("user-abc"));

        assertTrue(calendarGateway.authenticated, "Interactor should trigger authentication when credential missing");
        assertEquals(1, calendarGateway.created.size());
        assertEquals("user-abc", calendarGateway.created.get(0).getUserId());
        assertNotNull(outputBoundary.successData);
        assertNull(outputBoundary.failureMessage);
    }

    /**
     * Confirms failures from the calendar gateway surface through the output boundary.
     */
    @Test
    void reportsFailureWhenSyncThrows() {
        FakeTaskGateway taskGateway = new FakeTaskGateway();
        taskGateway.addForUser("user-fail", buildTask("Failing Task"));

        FakeCalendarGateway calendarGateway = new FakeCalendarGateway();
        calendarGateway.storedCredential = true;
        calendarGateway.throwOnCreate = true;
        CapturingOutputBoundary outputBoundary = new CapturingOutputBoundary();

        SyncToGoogleCalendarInteractor interactor = new SyncToGoogleCalendarInteractor(
                taskGateway, calendarGateway, outputBoundary);

        interactor.execute(new SyncToGoogleCalendarInputData("user-fail"));

        assertNull(outputBoundary.successData);
        assertNotNull(outputBoundary.failureMessage);
        assertTrue(outputBoundary.failureMessage.startsWith("An error occurred during synchronization: "));
        assertTrue(outputBoundary.failureMessage.contains("create failure"));
    }

    private Task buildTask(String name) {
        return new TaskBuilder()
                .setTaskName(name)
                .setTaskGroup("group")
                .setDeadline(LocalDateTime.parse("2025-11-27T00:00:00"))
                .setDescription("desc")
                .build();
    }

    private static class CapturingOutputBoundary implements SyncToGoogleCalendarOutputBoundary {
        SyncToGoogleCalendarOutputData successData;
        String failureMessage;

        @Override
        public void prepareSuccessView(SyncToGoogleCalendarOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failureMessage = errorMessage;
        }
    }

    /**
     * Minimal in-memory task gateway to supply tasks by user ID.
     */
    private static class FakeTaskGateway implements TaskGateway {
        private final Map<String, ArrayList<Task>> tasksByUser = new HashMap<>();

        void addForUser(String userId, Task task) {
            tasksByUser.computeIfAbsent(userId, key -> new ArrayList<>()).add(task);
        }

        @Override
        public String addTask(String userId, Task task) {
            addForUser(userId, task);
            return "Task Added";
        }

        @Override
        public ArrayList<Task> fetchTasks(String userId) {
            ArrayList<Task> tasks = tasksByUser.get(userId);
            if (tasks == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(tasks);
        }

        @Override
        public boolean deleteTask(String userId, Task task) {
            ArrayList<Task> tasks = tasksByUser.get(userId);
            if (tasks == null) {
                return false;
            }
            return tasks.remove(task);
        }
    }

    /**
     * In-memory calendar gateway that records created events and simulates authentication and failures.
     */
    private static class FakeCalendarGateway implements CalendarGateway {
        final List<CreatedEvent> created = new ArrayList<>();
        boolean storedCredential;
        boolean authenticated;
        boolean throwOnCreate;

        @Override
        public String createEvent(String userId, entities.Completable task) {
            if (throwOnCreate) {
                throw new RuntimeException("create failure");
            }
            CreatedEvent event = new CreatedEvent(userId, task);
            created.add(event);
            return "id-" + created.size();
        }

        @Override
        public boolean updateEvent(String userId, String eventID, entities.Completable updatedTask) {
            return false;
        }

        @Override
        public boolean deleteEvent(String userId, String eventID) {
            return false;
        }

        @Override
        public List<Event> getEvents(String userID) {
            return List.of();
        }

        @Override
        public boolean hasStoredCredential(String userId) {
            return storedCredential;
        }

        @Override
        public void authenticateUser(String userId) {
            authenticated = true;
        }

        private static class CreatedEvent {
            private final String userId;
            private final entities.Completable completable;

            private CreatedEvent(String userId, entities.Completable completable) {
                this.userId = userId;
                this.completable = completable;
            }

            private String getUserId() {
                return userId;
            }

            private entities.Completable getCompletable() {
                return completable;
            }
        }
    }
}
