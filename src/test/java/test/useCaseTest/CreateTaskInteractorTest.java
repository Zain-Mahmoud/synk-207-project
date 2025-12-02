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
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 15, 23, 59),
                "School",
                false,
                1
        );

        interactor.execute(input);

        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Study", presenter.successData.getTaskName());
        assertEquals("testUser", presenter.successData.getUsername());
        assertEquals(1, taskGateway.fetchTasks("testUser").size());
        assertEquals("Study", taskGateway.fetchTasks("testUser").get(0).getName());
    }

    @Test
    void createTask_failsWhenUsernameIsEmpty() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "",
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 15, 23, 59),
                "School",
                false,
                1
        );

        interactor.execute(input);

        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenUsernameIsNull() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                null,
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 15, 23, 59),
                "School",
                false,
                1
        );

        interactor.execute(input);

        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenUsernameIsWhitespace() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "   ",
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 15, 23, 59),
                "School",
                false,
                1
        );

        interactor.execute(input);

        assertEquals("Username cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenTaskNameIsEmpty() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
                "",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 15, 23, 59),
                "School",
                false,
                1
        );

        interactor.execute(input);

        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenTaskNameIsNull() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
                null,
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 15, 23, 59),
                "School",
                false,
                1
        );

        interactor.execute(input);

        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenTaskNameIsWhitespace() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
                "   ",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 15, 23, 59),
                "School",
                false,
                1
        );

        interactor.execute(input);

        assertEquals("Task name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createTask_failsWhenTaskAlreadyExists() {
        InMemoryTaskDataAccessObject taskGateway = new InMemoryTaskDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateTaskInteractor interactor = new CreateTaskInteractor(taskGateway, presenter);

        CreateTaskInputData input = new CreateTaskInputData(
                "testUser",
                "Study",
                "Study for exam",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 15, 23, 59),
                "School",
                false,
                1
        );

        interactor.execute(input);
        interactor.execute(input);

        assertEquals("Task already exists.", presenter.failMessage);
    }
}
