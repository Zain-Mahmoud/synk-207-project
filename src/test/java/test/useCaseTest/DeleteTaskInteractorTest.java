package test.useCaseTest;

import data_access.InMemoryTaskDataAccessObject;
import entities.Task;
import entities.TaskBuilder;
import org.junit.jupiter.api.Test;
import use_case.delete_task.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeleteTaskInteractorTest {

    // Adapter to bridge DeleteTaskUserDataAccess and InMemoryTaskDataAccessObject
    static class InMemoryTaskDAOAdapter implements DeleteTaskUserDataAccess {
        private final InMemoryTaskDataAccessObject dao;

        public InMemoryTaskDAOAdapter(InMemoryTaskDataAccessObject dao) {
            this.dao = dao;
        }

        @Override
        public void deleteTask(String username, String taskName) {
            // Fetch task to delete
            Task taskToDelete = dao.fetchTasks(username).stream()
                    .filter(t -> t.getName().equals(taskName))
                    .findFirst()
                    .orElse(null);

            if (taskToDelete != null) {
                dao.deleteTask(username, taskToDelete);
            }
        }

        @Override
        public boolean existsTaskByName(String username, String taskName) {
            return !dao.fetchTasks(username).stream()
                    .filter(t -> t.getName().equals(taskName))
                    .findFirst()
                    .isEmpty();
        }

        // Helper for setup
        public void addTask(String username, Task task) {
            dao.addTask(username, task);
        }
    }

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
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, adapter);

        // Setup: Add a task
        Task task = new TaskBuilder()
                .setTaskName("Study")
                .setDeadline(LocalDateTime.now())
                .build();
        adapter.addTask("roy", task);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "Study");

        // Act
        interactor.execute(input);

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Study", presenter.successData.getTaskName());

        // Verify it's gone from real DAO
        assertEquals(0, realDAO.fetchTasks("roy").size());
    }

    @Test
    void deleteTask_failsWhenUsernameIsEmpty() {
        // Arrange
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, adapter);

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
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, adapter);

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
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, adapter);

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
        DeleteTaskUserDataAccess throwingDAO = new DeleteTaskUserDataAccess() {
            @Override
            public void deleteTask(String username, String taskName) {
                throw new RuntimeException("Database error");
            }

            @Override
            public boolean existsTaskByName(String username, String taskName) {
                return true; // Pretend it exists
            }
        };
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, throwingDAO);

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
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, adapter);

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
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, adapter);

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
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, adapter);

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
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteTaskInteractor interactor = new DeleteTaskInteractor(presenter, adapter);

        DeleteTaskInputData input = new DeleteTaskInputData("roy", "   ");

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }
}
