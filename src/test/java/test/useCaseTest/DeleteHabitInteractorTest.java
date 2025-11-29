package test.useCaseTest;

import data_access.InMemoryHabitDataAccessObject;
import entities.Habit;
import entities.HabitBuilder;
import org.junit.jupiter.api.Test;
import use_case.delete_habit.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeleteHabitInteractorTest {

    static class TestPresenter implements DeleteHabitOutputBoundary {
        String failMessage;
        DeleteHabitOutputData successData;

        @Override
        public void prepareSuccessView(DeleteHabitOutputData outputData) {
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
    void deleteHabit_successfullyDeletesHabit() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        // Setup: Add a habit
        Habit habit = new HabitBuilder()
                .setHabitName("Exercise")
                .setStartDateTime(LocalDateTime.now())
                .build();
        habitGateway.addHabit("roy", habit);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "Exercise");

        // Act
        interactor.execute(input);

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Exercise", presenter.successData.getHabitName());

        // Verify it's gone from gateway
        assertEquals(0, habitGateway.getHabitsForUser("roy").size());
    }

    @Test
    void deleteHabit_failsWhenNameIsEmpty() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "");

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteHabit_failsWhenHabitDoesNotExist() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "NonExistent");

        // Act
        interactor.execute(input);

        // Assert
        assertTrue(presenter.failMessage.contains("does not exist"));
        assertNull(presenter.successData);
    }

    @Test
    void deleteHabit_failsWhenDAOThrowsException() {
        // Arrange
        use_case.gateways.HabitGateway throwingGateway = new use_case.gateways.HabitGateway() {
            @Override
            public String addHabit(String userId, Habit habit) {
                return null;
            }

            @Override
            public java.util.ArrayList<Habit> fetchHabits(String userId) {
                return new java.util.ArrayList<>();
            }

            @Override
            public boolean deleteHabit(String userId, Habit habit) {
                throw new RuntimeException("Database error");
            }

            @Override
            public java.util.Map<String, java.util.List<Habit>> getAllUsersWithHabits() {
                return new java.util.HashMap<>();
            }

            @Override
            public java.util.List<String> getAllUsernames() {
                return new java.util.ArrayList<>();
            }

            @Override
            public java.util.List<Habit> getHabitsForUser(String username) {
                // Return a fake habit so existsByName check passes
                Habit fakeHabit = new HabitBuilder()
                        .setHabitName("Exercise")
                        .setStartDateTime(LocalDateTime.now())
                        .build();
                java.util.List<Habit> habits = new java.util.ArrayList<>();
                habits.add(fakeHabit);
                return habits;
            }
        };
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, throwingGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "Exercise");

        // Act
        interactor.execute(input);

        // Assert
        assertTrue(presenter.failMessage.contains("Failed to delete habit"));
        assertTrue(presenter.failMessage.contains("Database error"));
    }

    @Test
    void deleteHabit_failsWhenNameIsNull() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", null);

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteHabit_failsWhenNameIsWhitespace() {
        // Arrange
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "   ");

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }
}
