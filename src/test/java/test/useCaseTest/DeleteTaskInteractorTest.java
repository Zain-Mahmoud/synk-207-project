package test.useCaseTest;

import data_access.InMemoryTaskDataAccessObject;
import entities.Task;
import entities.TaskBuilder;
import org.junit.jupiter.api.Test;
import use_case.delete_task.*;

import java.time.LocalDateTime;

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
        // Arrange
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        // Setup: Add a task
        Task task = new TaskBuilder()
                .setTaskName("Study")
                .setDeadline(LocalDateTime.now())
                .build();
        taskGateway.addTask("roy", task);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "Study");

        // Act
        interactor.execute(input);

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Study", presenter.successData.getTaskName());

        // Verify it's gone from gateway
        assertEquals(0, taskGateway.fetchTasks("roy").size());
    }

    @Test
    void deleteTask_failsWhenUsernameIsEmpty() {
        // Arrange
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("", "Study");

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenTaskNameIsEmpty() {
        // Arrange
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "");

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenTaskDoesNotExist() {
        // Arrange
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "NonExistent");

        // Act
        interactor.execute(input);

        // Assert
        assertTrue(presenter.failMessage.contains("does not exist"));
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenDAOThrowsException() {
        // Arrange
        use_case.gateways.TaskGateway throwingGateway = new use_case.gateways.TaskGateway() {
            @Override
            public String addTask(String userId, Task task) {
                return null;
            }

            @Override
            public java.util.ArrayList<Task> fetchTasks(String userId) {
                // Return a fake task so existsByName check passes
                Task fakeTask = new TaskBuilder()
                        .setTaskName("Study")
                        .setDeadline(LocalDateTime.now())
                        .build();
                java.util.ArrayList<Task> tasks = new java.util.ArrayList<>();
                tasks.add(fakeTask);
                return tasks;
            }

            @Override
            public boolean deleteTask(String userId, Task task) {
                throw new RuntimeException("Database error");
            }
        };
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, throwingGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "Study");

        // Act
        interactor.execute(input);

        // Assert
        assertTrue(presenter.failMessage.contains("Failed to delete task"));
        assertTrue(presenter.failMessage.contains("Database error"));
    }

    @Test
    void deleteTask_failsWhenUsernameIsNull() {
        // Arrange
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData(null, "Study");

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenUsernameIsWhitespace() {
        // Arrange
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("   ", "Study");

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenTaskNameIsNull() {
        // Arrange
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", null);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteTask_failsWhenTaskNameIsWhitespace() {
        // Arrange
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, taskGateway);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "   ");

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }
}
