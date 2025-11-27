package test.useCaseTest;

import data_access.InMemoryTaskDataAccessObject;
import entities.Task;
import org.junit.jupiter.api.Test;
import use_case.create_task.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreateTaskInteractorTest {

    // Adapter to bridge CreateTaskUserDataAccessInterface and
    // InMemoryTaskDataAccessObject
    static class InMemoryTaskDAOAdapter implements CreateTaskUserDataAccessInterface {
        private final InMemoryTaskDataAccessObject dao;
        private final String testUser = "testUser"; // Hardcoded user for testing since interface lacks username

        public InMemoryTaskDAOAdapter(InMemoryTaskDataAccessObject dao) {
            this.dao = dao;
        }

        @Override
        public boolean existsByName(String taskName) {
            // Check if task exists for the test user
            return !dao.fetchTasks(testUser).stream()
                    .filter(t -> t.getName().equals(taskName))
                    .findFirst()
                    .isEmpty();
        }

        @Override
        public void save(Task task) {
            dao.addTask(testUser, task);
        }

        // Helper for setup
        public void addTask(String username, Task task) {
            dao.addTask(username, task);
        }
    }

    static class TestPresenter implements CreateTaskOutputBoundary {
        String failMessage;
        CreateTaskOutputData successData;

        @Override
        public void prepareSuccessView(CreateTaskOutputData outputData) {
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
    void createTask_successfullyCreatesTask() {
        // Arrange
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(adapter, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser", // Must match adapter's hardcoded user if we want to verify via realDAO easily,
                            // but interactor passes username to output data, not DAO save.
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "School",
                false,
                1);

        // Act
        interactor.execute(input);

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Study", presenter.successData.getTaskName());
        assertEquals("testUser", presenter.successData.getUsername());

        // Verify it's in the real DAO
        assertEquals(1, realDAO.fetchTasks("testUser").size());
        assertEquals("Study", realDAO.fetchTasks("testUser").get(0).getName());
    }

    @Test
    void createTask_failsWhenUsernameIsEmpty() {
        // Arrange
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(adapter, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "",
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "School",
                false,
                1);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenTaskNameIsEmpty() {
        // Arrange
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(adapter, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
                "",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "School",
                false,
                1);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenTaskAlreadyExists() {
        // Arrange
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(adapter, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "School",
                false,
                1);
        interactor.execute(input);

        // Act - Try again
        interactor.execute(input);

        // Assert
        assertEquals("Task already exists.", presenter.failMessage);
    }
    @Test
    void createTask_failsWhenUsernameIsNull() {
        // Arrange
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(adapter, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                null,
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "School",
                false,
                1);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenUsernameIsWhitespace() {
        // Arrange
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(adapter, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "   ",
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "School",
                false,
                1);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenTaskNameIsNull() {
        // Arrange
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(adapter, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
                null,
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "School",
                false,
                1);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenTaskNameIsWhitespace() {
        // Arrange
        InMemoryTaskDataAccessObject realDAO = new InMemoryTaskDataAccessObject();
        InMemoryTaskDAOAdapter adapter = new InMemoryTaskDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(adapter, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
                "   ",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "School",
                false,
                1);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }
}
