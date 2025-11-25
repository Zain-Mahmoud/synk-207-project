package useCaseTest;

import entities.Habit;
import org.junit.jupiter.api.Test;
import use_case.create_habit.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateHabitInteractorTest {

    static class InMemoryHabitDAO implements CreateHabitUserDataAccess {
        // username -> (habitName -> Habit)
        private final Map<String, Map<String, Habit>> data = new HashMap<>();

        @Override
        public void save(String username, Habit habit) {
            data.computeIfAbsent(username, u -> new HashMap<>())
                    .put(habit.getName(), habit);
        }

        @Override
        public boolean existsByName(String username, String habitName) {
            return data.containsKey(username)
                    && data.get(username).containsKey(habitName);
        }

        Habit find(String username, String habitName) {
            Map<String, Habit> habits = data.get(username);
            if (habits == null) return null;
            return habits.get(habitName);
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
        InMemoryHabitDAO habitDAO = new InMemoryHabitDAO();
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor =
                new CreateHabitInteractor(habitDAO, presenter);

        CreateHabitInputData input = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0),
                "Health",
                0,
                5
        );

        // Act
        interactor.execute(input);

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Exercise", presenter.successData.getHabitName());

        Habit saved = habitDAO.find("roy", "Exercise");
        assertNotNull(saved);
        assertEquals("Exercise", saved.getName());
    }
}
