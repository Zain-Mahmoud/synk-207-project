package test.useCaseTest;

import data_access.InMemoryHabitDataAccessObject;
import entities.Habit;
import entities.HabitBuilder;
import org.junit.jupiter.api.Test;
import use_case.create_habit.*;
import use_case.gateways.HabitGateway;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                5
        );

        interactor.execute(input);

        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Exercise", presenter.successData.getHabitName());
        assertEquals(1, habitGateway.getHabitsForUser("roy").size());
        assertEquals("Exercise", habitGateway.getHabitsForUser("roy").get(0).getName());
    }

    @Test
    void createHabit_failsWhenNameIsEmpty() {
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
                5
        );

        interactor.execute(input);

        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createHabit_failsWhenNameIsWhitespace() {
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
                5
        );

        interactor.execute(input);

        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createHabit_failsWhenNameIsNull() {
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
                5
        );

        interactor.execute(input);

        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createHabit_failsWhenHabitAlreadyExists() {
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        CreateHabitInteractor interactor = new CreateHabitInteractor(habitGateway, presenter);

        CreateHabitInputData first = new CreateHabitInputData(
                "roy",
                "Exercise",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                1,
                "Health",
                0,
                5
        );
        interactor.execute(first);

        interactor.execute(first);

        assertEquals("Habit with name 'Exercise' already exists.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void createHabit_failsWhenGatewayThrowsRuntimeException() {
        HabitGateway throwingGateway = new HabitGateway() {
            @Override
            public String addHabit(String userId, Habit habit) {
                throw new RuntimeException("Database error");
            }

            @Override
            public ArrayList<Habit> fetchHabits(String userId) {
                return new ArrayList<>();
            }

            @Override
            public boolean deleteHabit(String userId, Habit habit) {
                return false;
            }

            @Override
            public HashMap<String, List<Habit>> getAllUsersWithHabits() {
                return new HashMap<>();
            }

            @Override
            public List<String> getAllUsernames() {
                return new ArrayList<>();
            }

            @Override
            public List<Habit> getHabitsForUser(String username) {
                return new ArrayList<>();
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
                5
        );

        interactor.execute(input);

        assertNotNull(presenter.failMessage);
        assertTrue(presenter.failMessage.contains("Failed to create habit:"));
        assertTrue(presenter.failMessage.contains("Database error"));
    }

    @Test
    void createHabit_failsWhenGatewayThrowsIllegalStateException() {
        HabitGateway throwingGateway = new HabitGateway() {
            @Override
            public String addHabit(String userId, Habit habit) {
                throw new IllegalStateException("DAO State Error");
            }

            @Override
            public ArrayList<Habit> fetchHabits(String userId) {
                return new ArrayList<>();
            }

            @Override
            public boolean deleteHabit(String userId, Habit habit) {
                return false;
            }

            @Override
            public HashMap<String, List<Habit>> getAllUsersWithHabits() {
                return new HashMap<>();
            }

            @Override
            public List<String> getAllUsernames() {
                return new ArrayList<>();
            }

            @Override
            public List<Habit> getHabitsForUser(String username) {
                return new ArrayList<>();
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
                5
        );

        interactor.execute(input);

        assertNotNull(presenter.failMessage);
        assertTrue(presenter.failMessage.contains("Failed to create habit: DAO State Error"));
    }
}
