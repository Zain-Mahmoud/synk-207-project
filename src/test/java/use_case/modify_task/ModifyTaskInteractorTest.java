package use_case.modify_task;

import data_access.InMemoryTaskDataAccessObject;
import entities.Task;
import entities.TaskBuilder;
import org.junit.jupiter.api.Test;
import use_case.gateways.TaskGateway;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ModifyTaskInteractorTest {
    // This format is only used for setting up the initial task's LocalDateTime object
    final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final String USER_ID = "testUser";
    private final String FUTURE_DATETIME = "2027-01-05T08:00:00";
    private final String OLD_DEADLINE = "2027-01-05T08:00:00";
    private final String PAST_DATETIME = "2024-01-05T08:00:00";
    // New date string for testing the new custom format
    private final String NEW_CUSTOM_DATETIME_STRING = "01 December, 2027 10:30";

    private Task createInitialTask() {
        return new TaskBuilder()
                .setTaskName("Old Task Name")
                .setPriority(1)
                .setDeadline(LocalDateTime.parse(OLD_DEADLINE, INPUT_FORMAT))
                .setStatus(false)
                .setTaskGroup("Work")
                .setDescription("Initial Description")
                .build();
    }


    @Test
    void successTest() {

        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = createInitialTask();
        taskGateway.addTask(USER_ID, oldTask);


        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDueDate().toString(), null, oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "New Task Name", "5", FUTURE_DATETIME, "", true, "Personal", "New Description",
                USER_ID
        );


        ModifyTaskOutputBoundary successPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {

                ArrayList<Task> tasks = taskGateway.fetchTasks(USER_ID);
                assertEquals(1, tasks.size());
                Task modifiedTask = tasks.get(0);


                assertEquals("New Task Name", modifiedTask.getName());
                assertEquals(5, modifiedTask.getPriority());
                assertEquals(LocalDateTime.parse(FUTURE_DATETIME, INPUT_FORMAT), modifiedTask.getDueDate());
                assertTrue(modifiedTask.getStatus());
                assertEquals("Personal", modifiedTask.getTaskGroup());
                assertEquals("New Description", modifiedTask.getDescription());
                assertEquals(1, outputData.getTaskList().toArray().length);
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Modification should have succeeded: " + errorMessage);
            }

            @Override
            public void switchToTaskListView() {

            }
        };


        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(successPresenter, taskGateway);
        interactor.execute(inputData);

    }

    /**
     * Test case to cover the execution path where the task name is UNCHANGED,
     * which means that when the task list is iterated, the condition
     * `task.getName().equals(modifiedTask.getName())` is TRUE and `!task.equals(oldTask)` is FALSE,
     * ensuring the failure block is skipped. This is the desired coverage target.
     */
    @Test
    void successTest_ModifyingOtherFieldsButKeepingName() {
        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = new TaskBuilder()
                .setTaskName("Old Task Name")
                .setPriority(1)
                .setDeadline(LocalDateTime.parse(OLD_DEADLINE, INPUT_FORMAT))
                .setStartTime(LocalDateTime.parse(OLD_DEADLINE, INPUT_FORMAT))
                .setStatus(false)
                .setTaskGroup("Work")
                .setDescription("Initial Description")
                .build();
           taskGateway.addTask(USER_ID, oldTask);

        // Input data keeps the same name but changes priority and status
        String UNCHANGED_NAME = oldTask.getName();

        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDueDate().toString(), oldTask.getStartTime().toString(), oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                UNCHANGED_NAME, "10", FUTURE_DATETIME, OLD_DEADLINE, true, "Personal", "New Description",
                USER_ID
        );


        ModifyTaskOutputBoundary successPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                ArrayList<Task> tasks = taskGateway.fetchTasks(USER_ID);
                assertEquals(1, tasks.size());
                Task modifiedTask = tasks.get(0);

                // Assert that the name remained unchanged
                assertEquals(UNCHANGED_NAME, modifiedTask.getName());
                // Assert that other fields were modified
                assertEquals(10, modifiedTask.getPriority());
                assertTrue(modifiedTask.getStatus());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Modification should have succeeded even when name is unchanged: " + errorMessage);
            }

            @Override
            public void switchToTaskListView() {

            }
        };


        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(successPresenter, taskGateway);
        interactor.execute(inputData);
    }

    @Test
    void successTest_WithNewDateFormat() {
        // Test case to ensure the new custom format is parsed successfully
        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = createInitialTask();
        taskGateway.addTask(USER_ID, oldTask);

        // Define a formatter matching the new custom format for comparison
        final DateTimeFormatter CUSTOM_FORMAT = DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm", Locale.ENGLISH);

        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDueDate().toString(), "", oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "New Task Name Custom", "4", NEW_CUSTOM_DATETIME_STRING, NEW_CUSTOM_DATETIME_STRING, true, "Personal", "New Description",
                USER_ID
        );


        ModifyTaskOutputBoundary successPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                ArrayList<Task> tasks = taskGateway.fetchTasks(USER_ID);
                assertEquals(1, tasks.size());
                Task modifiedTask = tasks.get(0);

                assertEquals("New Task Name Custom", modifiedTask.getName());
                // Assert that the parsed date matches what is expected from the custom format
                assertEquals(LocalDateTime.parse(NEW_CUSTOM_DATETIME_STRING, CUSTOM_FORMAT), modifiedTask.getDueDate());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Modification should have succeeded with new date format: " + errorMessage);
            }

            @Override
            public void switchToTaskListView() {

            }
        };


        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(successPresenter, taskGateway);
        interactor.execute(inputData);
    }

    /**
     * New test case to ensure modification succeeds when newStartDateTime is an empty string.
     */
    @Test
    void successTest_NewStartDateTimeIsBlank() {
        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = createInitialTask();
        taskGateway.addTask(USER_ID, oldTask);

        String BLANK_START_TIME = ""; // The input to test

        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDueDate().toString(), "", oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "Task with Blank Start Time", "2", FUTURE_DATETIME, BLANK_START_TIME, true, "School", "Description updated for blank start time",
                USER_ID
        );

        ModifyTaskOutputBoundary successPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                ArrayList<Task> tasks = taskGateway.fetchTasks(USER_ID);
                assertEquals(1, tasks.size());
                Task modifiedTask = tasks.get(0);

                // Assert that other fields were modified successfully and no failure occurred
                assertEquals("Task with Blank Start Time", modifiedTask.getName());
                assertEquals(2, modifiedTask.getPriority());
                assertTrue(modifiedTask.getStatus());
                assertEquals("School", modifiedTask.getTaskGroup());
                assertEquals(LocalDateTime.parse(FUTURE_DATETIME, INPUT_FORMAT), modifiedTask.getDueDate());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Modification should have succeeded when newStartDateTime is blank: " + errorMessage);
            }

            @Override
            public void switchToTaskListView() {
                // Pass
            }
        };

        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(successPresenter, taskGateway);
        interactor.execute(inputData);
    }

    /**
     * New test case to ensure modification succeeds when newStartDateTime is null.
     */
    @Test
    void successTest_NewStartDateTimeIsNull() {
        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = createInitialTask();
        taskGateway.addTask(USER_ID, oldTask);

        String NULL_START_TIME = null; // The input to test

        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDueDate().toString(), "", oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "Task with Null Start Time", "3", FUTURE_DATETIME, NULL_START_TIME, false, "Home", "Description for null start time",
                USER_ID
        );

        ModifyTaskOutputBoundary successPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                ArrayList<Task> tasks = taskGateway.fetchTasks(USER_ID);
                assertEquals(1, tasks.size());
                Task modifiedTask = tasks.get(0);

                // Assert that other fields were modified successfully and no failure occurred
                assertEquals("Task with Null Start Time", modifiedTask.getName());
                assertEquals(3, modifiedTask.getPriority());
                assertFalse(modifiedTask.getStatus());
                assertEquals("Home", modifiedTask.getTaskGroup());
                assertEquals(LocalDateTime.parse(FUTURE_DATETIME, INPUT_FORMAT), modifiedTask.getDueDate());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Modification should have succeeded when newStartDateTime is null: " + errorMessage);
            }

            @Override
            public void switchToTaskListView() {
                // Pass
            }
        };

        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(successPresenter, taskGateway);
        interactor.execute(inputData);
    }

    @Test
    void successSwitchToTaskList() {

        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = createInitialTask();
        taskGateway.addTask(USER_ID, oldTask);


        ModifyTaskOutputBoundary successPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                fail("Tasks and habits list should be displayed");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Tasks and habits list should be displayed");
            }

            @Override
            public void switchToTaskListView() {
                // This test passes if this method is called.
            }
        };


        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(successPresenter, taskGateway);
        interactor.switchToTaskListView();
    }


    @Test
    void failTest_InvalidDeadlineFormat() {

        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = createInitialTask();
        taskGateway.addTask(USER_ID, oldTask);


        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDueDate().toString(), "", oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "New Task", "1", "2025/11/27", "", false, "Group", "Desc",
                USER_ID
        );


        ModifyTaskOutputBoundary failPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                fail("Modification should have failed due to invalid deadline format.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // UPDATED EXPECTED ERROR MESSAGE to reflect multiple supported formats
                assertEquals("Invalid date/time format. Supported formats are: 'dd MMMM, yyyy HH:mm' (e.g., 01 February, 2026 06:00) or 'YYYY-MM-DDTHH:MM[:SS]' (e.g., 2026-02-01T06:00:00).", errorMessage);
            }

            @Override
            public void switchToTaskListView() {

            }
        };


        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(failPresenter, taskGateway);
        interactor.execute(inputData);
    }

    @Test
    void failTest_InvalidPriorityFormat() {

        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = createInitialTask();
        taskGateway.addTask(USER_ID, oldTask);


        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDueDate().toString(), "", oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "New Task", "High", FUTURE_DATETIME, "", false, "Group", "Desc",
                USER_ID
        );


        ModifyTaskOutputBoundary failPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                fail("Modification should have failed due to invalid priority format.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Invalid Priority. Must be a whole number.", errorMessage);
            }

            @Override
            public void switchToTaskListView() {

            }
        };


        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(failPresenter, taskGateway);
        interactor.execute(inputData);
    }

    @Test
    void failTest_InvalidOldPriorityFormat() {

        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = createInitialTask();
        taskGateway.addTask(USER_ID, oldTask);


        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTask.getName(), "5", oldTask.getDueDate().toString(), "", oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "New Task", "High", FUTURE_DATETIME, "", false, "Group", "Desc",
                USER_ID
        );


        ModifyTaskOutputBoundary failPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                fail("Modification should have failed due to invalid priority format.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Invalid Priority. Must be a whole number.", errorMessage);
            }

            @Override
            public void switchToTaskListView() {

            }
        };


        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(failPresenter, taskGateway);
        interactor.execute(inputData);
    }
    @Test
    void failTest_NewDeadlineInPast() {
        // 1. Setup initial state
        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTask = createInitialTask();
        taskGateway.addTask(USER_ID, oldTask);

        // 2. Define Input Data with a deadline in the past
        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDueDate().toString(), "", oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "Past Task", "1", PAST_DATETIME, "", false, "Group", "Desc",
                USER_ID
        );

        // 3. Mock Fail Presenter
        ModifyTaskOutputBoundary failPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                fail("Modification should have failed because the new deadline is in the past.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("New deadline cannot be in the past.", errorMessage);
            }

            @Override
            public void switchToTaskListView() {
                // Pass
            }
        };

        // 4. Run the Interactor
        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(failPresenter, taskGateway);
        interactor.execute(inputData);
    }
    @Test
    void failTest_DuplicateTaskName() {

        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        Task oldTaskToModify = createInitialTask();
        Task existingTask = new TaskBuilder()
                .setTaskName("Existing Task Name")
                .setPriority(2)
                // Use INPUT_FORMAT for consistent parsing
                .setDeadline(LocalDateTime.parse(FUTURE_DATETIME, INPUT_FORMAT))
                .setStatus(false)
                .setTaskGroup("Other Group")
                .setDescription("Already exists")
                .build();

        Task existingTask2 = new TaskBuilder()
                .setTaskName("Existing Task Name2")
                .setPriority(2)
                // Use INPUT_FORMAT for consistent parsing
                .setDeadline(LocalDateTime.parse(FUTURE_DATETIME, INPUT_FORMAT))
                .setStatus(false)
                .setTaskGroup("Other Group")
                .setDescription("Already exists")
                .build();

        taskGateway.addTask(USER_ID, existingTask2);
        taskGateway.addTask(USER_ID, oldTaskToModify);
        taskGateway.addTask(USER_ID, existingTask);


        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTaskToModify.getName(), Integer.toString(oldTaskToModify.getPriority()), oldTaskToModify.getDueDate().toString(), "", oldTaskToModify.getStatus(), oldTaskToModify.getTaskGroup(), oldTaskToModify.getDescription(), existingTask.getName(), "3", FUTURE_DATETIME, "",
                true, "Group", "Desc",
                USER_ID
        );


        ModifyTaskOutputBoundary failPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                fail("Modification should have failed due to duplicate task name.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Task already exists", errorMessage);
            }

            @Override
            public void switchToTaskListView() {

            }
        };


        ModifyTaskInputBoundary interactor = new ModifyTaskInteractor(failPresenter, taskGateway);
        interactor.execute(inputData);
    }
}