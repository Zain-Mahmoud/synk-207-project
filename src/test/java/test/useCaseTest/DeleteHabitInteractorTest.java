package test.useCaseTest;

import data_access.InMemoryHabitDataAccessObject;
import entities.Habit;
import entities.HabitBuilder;
import org.junit.jupiter.api.Test;
import use_case.delete_habit.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DeleteHabitInteractorTest {

    // Adapter to bridge DeleteHabitUserDataAccess and InMemoryHabitDataAccessObject
    static class InMemoryHabitDAOAdapter implements DeleteHabitUserDataAccess {
        private final InMemoryHabitDataAccessObject dao;

        public InMemoryHabitDAOAdapter(InMemoryHabitDataAccessObject dao) {
            this.dao = dao;
        }

        @Override
        public void deleteHabit(String username, String habitName) {
            // Need to find the habit object first because InMemoryDAO requires Habit object
            // to delete
            // But wait, InMemoryHabitDataAccessObject.deleteHabit takes (String userId,
            // Habit habit)
            // The interface DeleteHabitUserDataAccess takes (String username, String habit)

            // We need to fetch the habit first.
            Habit habitToDelete = dao.getHabitsForUser(username).stream()
                    .filter(h -> h.getName().equals(habitName))
                    .findFirst()
                    .orElse(null);

            if (habitToDelete != null) {
                dao.deleteHabit(username, habitToDelete);
            } else {
                // If it doesn't exist, the interactor should have checked existsByName first.
                // But if we are here, maybe we should throw or do nothing?
                // The DAO contract in interactor implies void return.
                // If the interactor logic is correct, it checks existsByName before calling
                // delete.
            }
        }

        @Override
        public boolean existsByName(String username, String habitName) {
            return !dao.getHabitsForUser(username).stream()
                    .filter(h -> h.getName().equals(habitName))
                    .findFirst()
                    .isEmpty();
        }

        // Helper to add habit for setup
        public void addHabit(String username, Habit habit) {
            dao.addHabit(username, habit);
        }
    }

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
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, adapter);

        // Setup: Add a habit
        Habit habit = new HabitBuilder()
                .setHabitName("Exercise")
                .setStartDateTime(LocalDateTime.now())
                .build();
        adapter.addHabit("roy", habit);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "Exercise");

        // Act
        interactor.execute(input);

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Exercise", presenter.successData.getHabitName());

        // Verify it's gone from real DAO
        assertEquals(0, realDAO.getHabitsForUser("roy").size());
    }

    @Test
    void deleteHabit_failsWhenNameIsEmpty() {
        // Arrange
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, adapter);

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
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, adapter);

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
        DeleteHabitUserDataAccess throwingDAO = new DeleteHabitUserDataAccess() {
            @Override
            public void deleteHabit(String username, String habit) {
                throw new RuntimeException("Database error");
            }

            @Override
            public boolean existsByName(String username, String habitName) {
                return true; // Pretend it exists to reach delete
            }
        };
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, throwingDAO);

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
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, adapter);

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
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, adapter);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "   ");

        // Act
        interactor.execute(input);

        // Assert
        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }
}
