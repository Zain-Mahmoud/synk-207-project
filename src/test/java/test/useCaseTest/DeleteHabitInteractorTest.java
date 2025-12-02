package test.useCaseTest;

import data_access.InMemoryHabitDataAccessObject;
import entities.Habit;
import entities.HabitBuilder;
import org.junit.jupiter.api.Test;
import use_case.delete_habit.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        Habit habit = new HabitBuilder()
                .setHabitName("Exercise")
                .setStartDateTime(LocalDateTime.now())
                .build();
        habitGateway.addHabit("roy", habit);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "Exercise");
        interactor.execute(input);

        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertEquals("Exercise", presenter.successData.getHabitName());
        assertEquals(0, habitGateway.getHabitsForUser("roy").size());
    }

    @Test
    void deleteHabit_failsWhenNameIsEmpty() {
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "");
        interactor.execute(input);

        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteHabit_failsWhenNameIsNull() {
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", null);
        interactor.execute(input);

        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteHabit_failsWhenNameIsWhitespace() {
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "   ");
        interactor.execute(input);

        assertEquals("Habit name cannot be empty.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void deleteHabit_failsWhenHabitDoesNotExist() {
        InMemoryHabitDataAccessObject habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, habitGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "NonExistent");
        interactor.execute(input);

        assertNotNull(presenter.failMessage);
        assertTrue(presenter.failMessage.contains("does not exist"));
        assertNull(presenter.successData);
    }

    @Test
    void deleteHabit_failsWhenGatewayThrowsException() {
        use_case.gateways.HabitGateway throwingGateway = new use_case.gateways.HabitGateway() {
            @Override
            public String addHabit(String userId, Habit habit) {
                return null;
            }

            @Override
            public ArrayList<Habit> fetchHabits(String userId) {
                return new ArrayList<>();
            }

            @Override
            public boolean deleteHabit(String userId, Habit habit) {
                throw new RuntimeException("Database error");
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
                Habit fake = new HabitBuilder()
                        .setHabitName("Exercise")
                        .setStartDateTime(LocalDateTime.now())
                        .build();
                ArrayList<Habit> list = new ArrayList<>();
                list.add(fake);
                return list;
            }
        };

        TestPresenter presenter = new TestPresenter();
        DeleteHabitInteractor interactor = new DeleteHabitInteractor(presenter, throwingGateway);

        DeleteHabitInputData input = new DeleteHabitInputData("roy", "Exercise");
        interactor.execute(input);

        assertNotNull(presenter.failMessage);
        assertTrue(presenter.failMessage.contains("Failed to delete habit for user 'roy'"));
        assertTrue(presenter.failMessage.contains("Database error"));
    }
}
