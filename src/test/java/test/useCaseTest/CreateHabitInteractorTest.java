package test.useCaseTest;

import data_access.InMemoryHabitDataAccessObject;
import org.junit.jupiter.api.Test;
import use_case.create_habit.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreateHabitInteractorTest {

    static class TestPresenter implements CreateHabitOutputBoundary {
        String failMessage;
        CreateHabitOutputData successData;

        @Override
        public void prepareSuccessView(CreateHabitOutputData outputData) {
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
    void createHabit_successfullyCreatesHabit() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(habitGateway, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                1,
                "Health",
                0,
                5);

        // Act
        interactor.execute(input);

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Exercise", presenter.successData.getHabitName());

        // Verify it's in the gateway
        assertEquals(1, habitGateway.getHabitsForUser("roy").size());
        assertEquals("Exercise", habitGateway.getHabitsForUser("roy").get(0).getName());
    }

    @Test
    void createHabit_failsWhenNameIsEmpty() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(habitGateway, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                1,
                "Health",
                0,
                5);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createHabit_failsWhenHabitAlreadyExists() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(habitGateway, presenter);

        // Pre-populate DAO
        CreateHabitInputData input1 = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                1,
                "Health",
                0,
                5);
        interactor.execute(input1);

        // Act - Try to create same habit again
        interactor.execute(input1);

        // Assert
        assertEquals("Habit with name 'Exercise' already exists.", presenter.failMessage);
    }

    @Test
    void createHabit_failsWhenDAOThrowsException() {
        // Arrange
        use_case.gateways.HabitGateway throwingGateway = new use_case.gateways.HabitGateway() {
            @Override
            public String addHabit(String userId, entities.Habit habit) {
                throw new RuntimeException("Database error");
            }

            @Override
            public java.util.ArrayList<entities.Habit> fetchHabits(String userId) {
                return new java.util.ArrayList<>();
            }

            @Override
            public boolean deleteHabit(String userId, entities.Habit habit) {
                return false;
            }

            @Override
            public java.util.Map<String, java.util.List<entities.Habit>> getAllUsersWithHabits() {
                return new java.util.HashMap<>();
            }

            @Override
            public java.util.List<String> getAllUsernames() {
                return new java.util.ArrayList<>();
            }

            @Override
            public java.util.List<entities.Habit> getHabitsForUser(String username) {
                return new java.util.ArrayList<>();
            }
        };
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(throwingGateway, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                1,
                "Health",
                0,
                5);

        // Act
        interactor.execute(input);

        // Assert
        assertTrue(presenter.failMessage.contains("Failed to create habit: Database error"));
    }

    @Test
    void createHabit_failsWhenNameIsNull() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(habitGateway, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                null,
                LocalDateTime.of(2025, 1, 1, 10, 0),
                1,
                "Health",
                0,
                5);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createHabit_failsWhenNameIsWhitespace() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(habitGateway, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "   ",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                1,
                "Health",
                0,
                5);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createHabit_failsWhenBuilderThrowsIllegalStateException() {
        // Arrange
        use_case.gateways.HabitGateway throwingGateway = new use_case.gateways.HabitGateway() {
            @Override
            public String addHabit(String userId, entities.Habit habit) {
                throw new IllegalStateException("DAO State Error");
            }

            @Override
            public java.util.ArrayList<entities.Habit> fetchHabits(String userId) {
                return new java.util.ArrayList<>();
            }

            @Override
            public boolean deleteHabit(String userId, entities.Habit habit) {
                return false;
            }

            @Override
            public java.util.Map<String, java.util.List<entities.Habit>> getAllUsersWithHabits() {
                return new java.util.HashMap<>();
            }

            @Override
            public java.util.List<String> getAllUsernames() {
                return new java.util.ArrayList<>();
            }

            @Override
            public java.util.List<entities.Habit> getHabitsForUser(String username) {
                return new java.util.ArrayList<>();
            }
        };
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(throwingGateway, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                1,
                "Health",
                0,
                5);

        // Act
        interactor.execute(input);

        // Assert
        assertTrue(presenter.failMessage.contains("Failed to create habit: DAO State Error"));
    }
}
