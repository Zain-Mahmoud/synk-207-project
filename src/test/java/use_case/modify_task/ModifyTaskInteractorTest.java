package use_case.modify_task;

import data_access.InMemoryTaskDataAccessObject;
import entities.Task;
import entities.TaskBuilder;
import org.junit.jupiter.api.Test;
import use_case.gateways.TaskGateway;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ModifyTaskInteractorTest {

    private final String USER_ID = "testUser";
    private final String FUTURE_DATETIME = LocalDateTime.now().plusDays(5).withNano(0).toString();
    private final String OLD_DEADLINE = LocalDateTime.now().plusDays(10).withNano(0).toString();
    private final String PAST_DATETIME = LocalDateTime.now().minusDays(5).withNano(0).toString();

    private Task createInitialTask() {
        return new TaskBuilder()
                .setTaskName("Old Task Name")
                .setPriority(1)
                .setDeadline(LocalDateTime.parse(OLD_DEADLINE))
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
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDeadline().toString(), oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "New Task Name", "5", FUTURE_DATETIME, true, "Personal", "New Description",
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
                assertEquals(LocalDateTime.parse(FUTURE_DATETIME), modifiedTask.getDeadline());
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
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDeadline().toString(), oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "New Task", "1", "2025/11/27", false, "Group", "Desc",
                USER_ID
        );


        ModifyTaskOutputBoundary failPresenter = new ModifyTaskOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyTaskOutputData outputData) {
                fail("Modification should have failed due to invalid deadline format.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Invalid date/time format. Use ISO format (e.g., YYYY-MM-DDTHH:MM:SS) for dates.", errorMessage);
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
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDeadline().toString(), oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "New Task", "High", FUTURE_DATETIME, false, "Group", "Desc",
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
                oldTask.getName(), "5", oldTask.getDeadline().toString(), oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "New Task", "High", FUTURE_DATETIME, false, "Group", "Desc",
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
                oldTask.getName(), Integer.toString(oldTask.getPriority()), oldTask.getDeadline().toString(), oldTask.getStatus(), oldTask.getTaskGroup(), oldTask.getDescription(),
                "Past Task", "1", PAST_DATETIME, false, "Group", "Desc",
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
                .setDeadline(LocalDateTime.parse(FUTURE_DATETIME))
                .setStatus(false)
                .setTaskGroup("Other Group")
                .setDescription("Already exists")
                .build();

        Task existingTask2 = new TaskBuilder()
                .setTaskName("Existing Task Name2")
                .setPriority(2)
                .setDeadline(LocalDateTime.parse(FUTURE_DATETIME))
                .setStatus(false)
                .setTaskGroup("Other Group")
                .setDescription("Already exists")
                .build();

        taskGateway.addTask(USER_ID, existingTask2);
        taskGateway.addTask(USER_ID, oldTaskToModify);
        taskGateway.addTask(USER_ID, existingTask);


        ModifyTaskInputData inputData = new ModifyTaskInputData(
                oldTaskToModify.getName(), Integer.toString(oldTaskToModify.getPriority()), oldTaskToModify.getDeadline().toString(),
                oldTaskToModify.getStatus(), oldTaskToModify.getTaskGroup(), oldTaskToModify.getDescription(), existingTask.getName(), "3", FUTURE_DATETIME,
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