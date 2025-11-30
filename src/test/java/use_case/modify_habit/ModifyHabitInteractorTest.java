package use_case.modify_habit;

import data_access.InMemoryHabitDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entities.Habit;
import entities.HabitBuilder;
import entities.User;
import entities.UserFactory;
import org.junit.jupiter.api.Test;
import use_case.gateways.HabitGateway;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ModifyHabitInteractorTest {

    private final String USER_ID = "Zain";
    private final String FUTURE_ISO_DATETIME = "2025-11-27T00:00:00";
    private final String CUSTOM_DATETIME_STRING = "01 February, 2026 06:00";
    private final DateTimeFormatter CUSTOM_FORMAT = DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm", Locale.ENGLISH);

    private final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    private Habit createInitialHabit() {
        return new HabitBuilder()
                .setHabitName("Programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse(FUTURE_ISO_DATETIME))
                .setPriority(1)
                .setStatus(true)
                .setStreakCount(1)
                .build();
    }

    @Test
    void successTest_ModificationWithNewName() {
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        Habit oldHabit = createInitialHabit();
        habitGateway.addHabit(USER_ID, oldHabit);

        ModifyHabitInputData inputData = new ModifyHabitInputData(
                oldHabit.getName(),
                Integer.toString(oldHabit.getPriority()),
                oldHabit.getStatus(),
                oldHabit.getStartTime().format(ISO_FORMATTER), // FIX: Use explicit format
                Integer.toString(oldHabit.getStreakCount()),
                oldHabit.getHabitGroup(),
                Integer.toString(oldHabit.getFrequency()),

                "New Habit Name", // New Name
                "2", // New Priority
                true,
                FUTURE_ISO_DATETIME,
                "5", // New Streak
                "Personal",
                "7", // New Frequency
                USER_ID
        );

        ModifyHabitOutputBoundary successPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData outputData) {
                // Assert the outer list size
                ArrayList<ArrayList<String>> habitList = outputData.getHabitList();
                assertEquals(1, habitList.size());

                // Get the inner list of attributes for the modified habit
                ArrayList<String> modifiedHabitAttributes = habitList.get(0);

                // Assert that the attribute array contains the new, modified values
                assertTrue(modifiedHabitAttributes.contains("New Habit Name"), "Habit attributes should contain the new name.");
                assertTrue(modifiedHabitAttributes.contains("2"), "Habit attributes should contain the new priority '2'.");
                assertTrue(modifiedHabitAttributes.contains("5"), "Habit attributes should contain the new streak count '5'.");
                assertTrue(modifiedHabitAttributes.contains("7"), "Habit attributes should contain the new frequency '7'.");
                assertTrue(modifiedHabitAttributes.contains("Personal"), "Habit attributes should contain the new group 'Personal'.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Modification should have succeeded: " + errorMessage);
            }

            @Override
            public void switchToHabitListView() {
            }
        };

        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(successPresenter, habitGateway);
        interactor.execute(inputData);
    }


    /**
     * Test case to cover the specific coverage gap: Modify fields other than name,
     * which forces the condition `!habit.equals(oldHabit)` to be FALSE
     * when `habit.getName().equals(modifiedHabit.getName())` is TRUE.
     */
    @Test
    void successTest_ModifyingOtherFieldsButKeepingName() {
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        Habit oldHabit = createInitialHabit(); // Name: "Programming", Priority: 1, Streak: 1
        habitGateway.addHabit(USER_ID, oldHabit);

        String UNCHANGED_NAME = oldHabit.getName();
        String UNCHANGED_FREQUENCY = Integer.toString(oldHabit.getFrequency());

        // Input data keeps the same name but changes priority and streak
        ModifyHabitInputData inputData = new ModifyHabitInputData(
                oldHabit.getName(),
                Integer.toString(oldHabit.getPriority()),
                oldHabit.getStatus(),
                oldHabit.getStartTime().format(ISO_FORMATTER), // FIX: Use explicit format
                Integer.toString(oldHabit.getStreakCount()),
                oldHabit.getHabitGroup(),
                UNCHANGED_FREQUENCY,

                UNCHANGED_NAME, // Unchanged Name
                "5", // New Priority
                oldHabit.getStatus(),
                FUTURE_ISO_DATETIME,
                "10", // New Streak
                oldHabit.getHabitGroup(),
                UNCHANGED_FREQUENCY,
                USER_ID
        );

        ModifyHabitOutputBoundary successPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData outputData) {
                ArrayList<ArrayList<String>> habitList = outputData.getHabitList();
                assertEquals(1, habitList.size());
                ArrayList<String> modifiedHabitAttributes = habitList.get(0);

                // Assert that the name remained unchanged and is present
                assertTrue(modifiedHabitAttributes.contains(UNCHANGED_NAME), "Habit attributes should contain the unchanged name.");
                // Assert that other fields were modified
                assertTrue(modifiedHabitAttributes.contains("5"), "Habit attributes should contain the new priority '5'.");
                assertTrue(modifiedHabitAttributes.contains("10"), "Habit attributes should contain the new streak count '10'.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Modification should have succeeded even when name is unchanged: " + errorMessage);
            }

            @Override
            public void switchToHabitListView() {
            }
        };

        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(successPresenter, habitGateway);
        interactor.execute(inputData);
    }

    @Test
    void successTestSwitch() {
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        ModifyHabitOutputBoundary successPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData modifyHabitOutputData) {
                fail("Switch method should be called, not success view.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Switch method should be called, not fail view.");
            }

            @Override
            public void switchToHabitListView() {
                assertTrue(true); // Success by reaching this point
            }
        };
        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(successPresenter, habitGateway);
        interactor.switchToHabitListView();
    }

    @Test
    void failTest_InvalidNewDateFormat() {
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        Habit oldHabit = createInitialHabit();
        habitGateway.addHabit(USER_ID, oldHabit);

        ModifyHabitInputData inputData = new ModifyHabitInputData(
                oldHabit.getName(), "1", true, oldHabit.getStartTime().format(ISO_FORMATTER), "1", "Programming", "1", // FIX: Use explicit format for old date
                "New Habit", "2", true, "2025-11-2700:00:00", "5", // Invalid format for new date
                "Programming", "1", USER_ID);

        ModifyHabitOutputBoundary failPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData modifyHabitOutputData) {
                fail("Modification should have failed due to invalid date format.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // Assert the comprehensive error message
                assertEquals("Invalid date/time format. Supported formats are: 'dd MMMM, yyyy HH:mm' (e.g., 01 February, 2026 06:00) or 'YYYY-MM-DDTHH:MM:SS' (e.g., 2026-02-01T06:00:00).", errorMessage);
            }

            @Override
            public void switchToHabitListView() {
            }
        };
        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(failPresenter, habitGateway);
        interactor.execute(inputData);
    }

    @Test
    void failTest_InvalidOldDateFormat() {
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();

        ModifyHabitInputData inputData = new ModifyHabitInputData(
                "Old Habit", "1", true, "2025-11-2700:00:00", "1", "Programming", "1", // Invalid format for old date
                "New Habit", "2", true, FUTURE_ISO_DATETIME, "5",
                "Programming", "1", USER_ID);

        ModifyHabitOutputBoundary failPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData modifyHabitOutputData) {
                fail("Modification should have failed due to invalid date format.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // Assert the comprehensive error message
                assertEquals("Invalid date/time format. Supported formats are: 'dd MMMM, yyyy HH:mm' (e.g., 01 February, 2026 06:00) or 'YYYY-MM-DDTHH:MM:SS' (e.g., 2026-02-01T06:00:00).", errorMessage);
            }

            @Override
            public void switchToHabitListView() {
            }
        };
        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(failPresenter, habitGateway);
        interactor.execute(inputData);
    }

    @Test
    void failTest_InvalidNumberFormat() {
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        Habit oldHabit = createInitialHabit();
        habitGateway.addHabit(USER_ID, oldHabit);

        ModifyHabitInputData inputData = new ModifyHabitInputData(
                oldHabit.getName(), "1", true, oldHabit.getStartTime().format(ISO_FORMATTER), "1", "Programming", "1", // FIX: Use explicit format
                "New Habit", "wrong format", true, FUTURE_ISO_DATETIME, "5", // Invalid Priority
                "Programming", "1", USER_ID);

        ModifyHabitOutputBoundary failPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData modifyHabitOutputData) {
                fail("Modification should have failed due to invalid number format.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                // Assert the comprehensive error message for all number fields
                assertEquals("Invalid Priority, Streak Count, or Frequency value (must be a number).", errorMessage);
            }

            @Override
            public void switchToHabitListView() {
            }
        };

        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(failPresenter, habitGateway);
        interactor.execute(inputData);
    }


    @Test
    void failTest_DuplicateTaskName() {
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        Habit oldHabitToModify = createInitialHabit();
        Habit existingHabit = new HabitBuilder()
                .setHabitName("Studying")
                .setHabitGroup("Studying")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse(FUTURE_ISO_DATETIME))
                .setPriority(2)
                .setStatus(true)
                .setStreakCount(5)
                .build();

        habitGateway.addHabit(USER_ID, oldHabitToModify);
        habitGateway.addHabit(USER_ID, existingHabit);

        // Try to rename "Programming" to "Studying" (which already exists)
        ModifyHabitInputData inputData = new ModifyHabitInputData(
                oldHabitToModify.getName(),
                Integer.toString(oldHabitToModify.getPriority()),
                oldHabitToModify.getStatus(),
                oldHabitToModify.getStartTime().format(ISO_FORMATTER), // FIX: Use explicit format
                Integer.toString(oldHabitToModify.getStreakCount()),
                oldHabitToModify.getHabitGroup(),
                Integer.toString(oldHabitToModify.getFrequency()),
                existingHabit.getName(), "2", true, FUTURE_ISO_DATETIME, "5",
                "Programming", "1", USER_ID);

        ModifyHabitOutputBoundary failPresenter = new ModifyHabitOutputBoundary() {
            @Override
            public void prepareSuccessView(ModifyHabitOutputData modifyHabitOutputData) {
                fail("Modification should have failed due to duplicate habit name.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Habit already exists", errorMessage);
            }

            @Override
            public void switchToHabitListView() {
            }
        };

        ModifyHabitInputBoundary interactor = new ModifyHabitInteractor(failPresenter, habitGateway);
        interactor.execute(inputData);
    }
}