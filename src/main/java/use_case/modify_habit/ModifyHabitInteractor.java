package use_case.modify_habit;

import entities.Habit;
import entities.HabitBuilder;
import use_case.gateways.HabitGateway;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

public class ModifyHabitInteractor implements ModifyHabitInputBoundary {
    private final ModifyHabitOutputBoundary modifyHabitPresenter;
    private final HabitGateway habitDataAccessObject;

    // Define supported formatters
    private static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public ModifyHabitInteractor(ModifyHabitOutputBoundary modifyHabitPresenter, HabitGateway habitDataAccessObject) {
        this.modifyHabitPresenter = modifyHabitPresenter;
        this.habitDataAccessObject = habitDataAccessObject;
    }

    /**
     * Attempts to parse a date string using supported formatters (CUSTOM_FORMATTER then ISO_FORMATTER).
     * @param dateString The date string to parse.
     * @return The parsed LocalDateTime object.
     * @throws DateTimeParseException if no format matches.
     */
    private LocalDateTime parseDateString(String dateString) throws DateTimeParseException {
        try {
            return LocalDateTime.parse(dateString, CUSTOM_FORMATTER);
        } catch (DateTimeParseException e) {
            // If custom format fails, try the standard ISO format
            return LocalDateTime.parse(dateString, ISO_FORMATTER);
        }
    }

    public void execute(ModifyHabitInputData modifyHabitInputData) {
        String userID = modifyHabitInputData.getUserID();

        // Old Habit Data
        String oldHabitName = modifyHabitInputData.getOldHabitName();
        String oldPriority = modifyHabitInputData.getOldPriority();
        boolean oldHabitStatus = modifyHabitInputData.getOldHabitStatus();
        String oldStartDateTime = modifyHabitInputData.getOldStartDateTime();
        String oldStreakCount = modifyHabitInputData.getOldStreakCount();
        String oldHabitGroup = modifyHabitInputData.getOldHabitGroup();
        String oldFrequency = modifyHabitInputData.getOldFrequency();

        // New Habit Data
        String newHabitName = modifyHabitInputData.getNewHabitName();
        String newPriority = modifyHabitInputData.getNewPriority();
        boolean newHabitStatus = modifyHabitInputData.getNewHabitStatus();
        String newStartDateTime = modifyHabitInputData.getNewStartDateTime();
        String newStreakCount = modifyHabitInputData.getNewStreakCount();
        String newHabitGroup = modifyHabitInputData.getNewHabitGroup();
        String newFrequency = modifyHabitInputData.getNewFrequency();

        try {
            // 1. Format and Validation
            LocalDateTime newStartDateTimeFormatted = parseDateString(newStartDateTime);
            LocalDateTime oldStartDateTimeFormatted = parseDateString(oldStartDateTime);

            int newFrequencyFormatted = Integer.parseInt(newFrequency);
            int newPriorityFormatted = Integer.parseInt(newPriority);
            int newStreakCountFormatted = Integer.parseInt(newStreakCount);

            int oldFrequencyFormatted = Integer.parseInt(oldFrequency);
            int oldPriorityFormatted = Integer.parseInt(oldPriority);
            int oldStreakCountFormatted = Integer.parseInt(oldStreakCount);

            // 2. Build Old Habit (for deletion/comparison)
            final Habit oldHabit = new HabitBuilder()
                    .setHabitName(oldHabitName)
                    .setPriority(oldPriorityFormatted)
                    .setStatus(oldHabitStatus)
                    .setStartDateTime(oldStartDateTimeFormatted)
                    .setStreakCount(oldStreakCountFormatted)
                    .setHabitGroup(oldHabitGroup)
                    .setFrequency(oldFrequencyFormatted)
                    .build();

            // 3. Build Modified Habit
            final Habit modifiedHabit = oldHabit.clone();
            modifiedHabit.setHabitName(newHabitName);
            modifiedHabit.setPriority(newPriorityFormatted);
            modifiedHabit.setStatus(newHabitStatus);
            modifiedHabit.setStartDateTime(newStartDateTimeFormatted);
            modifiedHabit.setStreakCount(newStreakCountFormatted);
            modifiedHabit.setHabitGroup(newHabitGroup);
            modifiedHabit.setFrequency(newFrequencyFormatted);

            // 4. Check for duplicate habit name
            ArrayList<Habit> habitList = habitDataAccessObject.fetchHabits(userID);
            for (Habit habit : habitList) {
                // Check if the modified name already exists for a DIFFERENT habit
                if (habit.getName().equals(modifiedHabit.getName()) && !habit.equals(oldHabit)) {
                    modifyHabitPresenter.prepareFailView("Habit already exists");
                    return;
                }
            }

            // 5. Save changes
            habitDataAccessObject.deleteHabit(userID, oldHabit);
            habitDataAccessObject.addHabit(userID, modifiedHabit);

            // 6. Success
            modifyHabitPresenter.prepareSuccessView(new
                    ModifyHabitOutputData(habitDataAccessObject.fetchHabits(userID)));

        } catch (DateTimeParseException d) {
            modifyHabitPresenter.prepareFailView("Invalid date/time format. Supported formats are: 'dd MMMM, yyyy HH:mm' (e.g., 01 February, 2026 06:00) or 'YYYY-MM-DDTHH:MM:SS' (e.g., 2026-02-01T06:00:00).");
        } catch (NumberFormatException n) {
            modifyHabitPresenter.prepareFailView("Invalid Priority, Streak Count, or Frequency value (must be a number).");
        }
    }

    @Override
    public void switchToHabitListView() {
        modifyHabitPresenter.switchToHabitListView();
    }
}