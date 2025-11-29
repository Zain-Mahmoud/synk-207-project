package test.useCaseTest;

import data_access.InMemoryTaskDataAccessObject;
import org.junit.jupiter.api.Test;
import use_case.create_task.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreateTaskInteractorTest {

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
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
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

        // Verify it's in the gateway
        assertEquals(1, taskGateway.fetchTasks("testUser").size());
        assertEquals("Study", taskGateway.fetchTasks("testUser").get(0).getName());
    }

    @Test
    void createTask_failsWhenUsernameIsEmpty() {
        // Arrange
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

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
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

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
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

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
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

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
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

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
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

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
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

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
