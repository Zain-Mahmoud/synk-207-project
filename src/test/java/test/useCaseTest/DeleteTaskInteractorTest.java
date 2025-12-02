package test.useCaseTest;

import data_access.InMemoryTaskDataAccessObject;
import entities.Task;
import entities.TaskBuilder;
import org.junit.jupiter.api.Test;
import use_case.delete_task.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DeleteTaskInteractorTest {

    static class TestPresenter implements DeleteTaskOutputBoundary {
        String failMessage;
        DeleteTaskOutputData successData;

        @Override
        public void prepareSuccessView(DeleteTaskOutputData outputData) {
            this.successData = outputData;
            this.failMessage = null;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failMessage = errorMessage;
            this.successData = null;
        }
    }

    @Test
    void deleteTask_successfullyDeletesTask() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        Task task = new TaskBuilder()
                .setTaskName("Study")
                .setDeadline(LocalDateTime.now())
                .build();
        taskGateway.addTask("roy", task);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "Study");
        interactor.execute(input);

        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Study", presenter.successData.getTaskName());
        assertEquals(0, taskGateway.fetchTasks("roy").size());
    }

    @Test
    void deleteTask_failsWhenUsernameIsEmpty() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("", "Study");
        interactor.execute(input);

        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenUsernameIsNull() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData(null, "Study");
        interactor.execute(input);

        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenUsernameIsWhitespace() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("   ", "Study");
        interactor.execute(input);

        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenTaskNameIsEmpty() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "");
        interactor.execute(input);

        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenTaskNameIsNull() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", null);
        interactor.execute(input);

        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenTaskNameIsWhitespace() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "   ");
        interactor.execute(input);

        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenTaskDoesNotExist() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "NonExistent");
        interactor.execute(input);

        assertNotNull(presenter.failMessage);
        assertTrue(presenter.failMessage.contains("does not exist"));
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenGatewayThrowsException() {
        use_case.gateways.TaskGateway throwingGateway = new use_case.gateways.TaskGateway() {
            @Override
            public String addTask(String userId, Task task) {
                return null;
            }

            @Override
            public ArrayList<Task> fetchTasks(String userId) {
                Task fake = new TaskBuilder()
                        .setTaskName("Study")
                        .setDeadline(LocalDateTime.now())
                        .build();
                ArrayList<Task> list = new ArrayList<>();
                list.add(fake);
                return list;
            }

            @Override
            public boolean deleteTask(String userId, Task task) {
                throw new RuntimeException("Database error");
            }
        };

        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, throwingGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "Study");
        interactor.execute(input);

        assertNotNull(presenter.failMessage);
        assertTrue(presenter.failMessage.contains("Failed to delete task"));
        assertTrue(presenter.failMessage.contains("Database error"));
    }
}
