package test.useCaseTest;

import data_access.InMemoryHabitDataAccessObject;
import entities.Habit;
import org.junit.jupiter.api.Test;
import use_case.create_habit.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreateHabitInteractorTest {

    // Adapter to bridge CreateHabitUserDataAccess and InMemoryHabitDataAccessObject
    static class InMemoryHabitDAOAdapter implements CreateHabitUserDataAccess {
        private final InMemoryHabitDataAccessObject dao;

        public InMemoryHabitDAOAdapter(InMemoryHabitDataAccessObject dao) {
            this.dao = dao;
        }

        @Override
        public void save(String username, Habit habit) {
            dao.addHabit(username, habit);
        }

        @Override
        public boolean existsByName(String username, String habitName) {
            return !dao.getHabitsForUser(username).stream()
                    .filter(h -> h.getName().equals(habitName))
                    .findFirst()
                    .isEmpty();
        }
    }

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
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(adapter, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0),
                "Health",
                0,
                5);

        // Act
        interactor.execute(input);

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Exercise", presenter.successData.getHabitName());

        // Verify it's in the real DAO
        assertEquals(1, realDAO.getHabitsForUser("roy").size());
        assertEquals("Exercise", realDAO.getHabitsForUser("roy").get(0).getName());
    }

    @Test
    void createHabit_failsWhenNameIsEmpty() {
        // Arrange
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(adapter, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0),
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
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(adapter, presenter);

        // Pre-populate DAO
        CreateHabitInputData input1 = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0),
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
        CreateHabitUserDataAccess throwingDAO = new CreateHabitUserDataAccess() {
            @Override
            public void save(String username, Habit habit) {
                throw new RuntimeException("Database error");
            }

            @Override
            public boolean existsByName(String username, String habitName) {
                return false;
            }
        };
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(throwingDAO, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0),
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
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(adapter, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                null,
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0),
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
        InMemoryHabitDataAccessObject realDAO = new InMemoryHabitDataAccessObject();
        InMemoryHabitDAOAdapter adapter = new InMemoryHabitDAOAdapter(realDAO);
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(adapter, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "   ",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0),
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
        CreateHabitUserDataAccess throwingDAO = new CreateHabitUserDataAccess() {
            @Override
            public void save(String username, Habit habit) {
                throw new IllegalStateException("DAO State Error");
            }

            @Override
            public boolean existsByName(String username, String habitName) {
                return false;
            }
        };
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(throwingDAO, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0),
                "Health",
                0,
                5);

        // Act
        interactor.execute(input);

        // Assert
        assertTrue(presenter.failMessage.contains("Failed to create habit: DAO State Error"));
    }
}
