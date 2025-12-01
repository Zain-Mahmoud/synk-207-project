package use_case.modify_habit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import entities.Habit;
import entities.HabitBuilder;
import strategy.HabitValidationStrategy;
import strategy.ValidationStrategy;
import use_case.gateways.HabitGateway;

public class ModifyHabitInteractor implements ModifyHabitInputBoundary {

    private static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm",
            Locale.ENGLISH);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ModifyHabitOutputBoundary modifyHabitPresenter;
    private final HabitGateway habitDataAccessObject;

    private final ValidationStrategy<Habit> habitValidation;

    public ModifyHabitInteractor(ModifyHabitOutputBoundary modifyHabitPresenter, HabitGateway habitDataAccessObject) {
        this.modifyHabitPresenter = modifyHabitPresenter;
        this.habitDataAccessObject = habitDataAccessObject;
        this.habitValidation = new HabitValidationStrategy(habitDataAccessObject);
    }

    /**
     * Attempts to parse a date string using supported formatters (CUSTOM_FORMATTER then ISO_FORMATTER).
     * @param dateString The date string to parse.
     * @return The parsed LocalDateTime object.
     * @throws DateTimeParseException if no format matches.
     */
    private LocalDateTime parseDateString(String dateString) {
        try {
            return LocalDateTime.parse(dateString, CUSTOM_FORMATTER);
        }
        catch (DateTimeParseException dateParseException) {
            return LocalDateTime.parse(dateString, ISO_FORMATTER);
        }
    }

    /**
     * Executes the ModifyHabit use case.
     * @param modifyHabitInputData the input data.
     */
    public void execute(ModifyHabitInputData modifyHabitInputData) {
        final String userID = modifyHabitInputData.getUserID();

        final String oldHabitName = modifyHabitInputData.getOldHabitName();
        final String oldPriority = modifyHabitInputData.getOldPriority();
        final boolean oldHabitStatus = modifyHabitInputData.getOldHabitStatus();
        final String oldStartDateTime = modifyHabitInputData.getOldStartDateTime();
        final String oldStreakCount = modifyHabitInputData.getOldStreakCount();
        final String oldHabitGroup = modifyHabitInputData.getOldHabitGroup();
        final String oldFrequency = modifyHabitInputData.getOldFrequency();

        final String newHabitName = modifyHabitInputData.getNewHabitName();
        final String newPriority = modifyHabitInputData.getNewPriority();
        final boolean newHabitStatus = modifyHabitInputData.getNewHabitStatus();
        final String newStartDateTime = modifyHabitInputData.getNewStartDateTime();
        final String newStreakCount = modifyHabitInputData.getNewStreakCount();
        final String newHabitGroup = modifyHabitInputData.getNewHabitGroup();
        final String newFrequency = modifyHabitInputData.getNewFrequency();

        try {
            final LocalDateTime newStartDateTimeFormatted = parseDateString(newStartDateTime);
            final LocalDateTime oldStartDateTimeFormatted = parseDateString(oldStartDateTime);

            final int newFrequencyFormatted = Integer.parseInt(newFrequency);
            final int newPriorityFormatted = Integer.parseInt(newPriority);
            final int newStreakCountFormatted = Integer.parseInt(newStreakCount);

            final int oldFrequencyFormatted = Integer.parseInt(oldFrequency);
            final int oldPriorityFormatted = Integer.parseInt(oldPriority);
            final int oldStreakCountFormatted = Integer.parseInt(oldStreakCount);

            final Habit oldHabit = new HabitBuilder()
                    .setHabitName(oldHabitName)
                    .setPriority(oldPriorityFormatted)
                    .setStatus(oldHabitStatus)
                    .setStartDateTime(oldStartDateTimeFormatted)
                    .setStreakCount(oldStreakCountFormatted)
                    .setHabitGroup(oldHabitGroup)
                    .setFrequency(oldFrequencyFormatted)
                    .build();

            final Habit modifiedHabit = oldHabit.clone();
            modifiedHabit.setHabitName(newHabitName);
            modifiedHabit.setPriority(newPriorityFormatted);
            modifiedHabit.setStatus(newHabitStatus);
            modifiedHabit.setStartDateTime(newStartDateTimeFormatted);
            modifiedHabit.setStreakCount(newStreakCountFormatted);
            modifiedHabit.setHabitGroup(newHabitGroup);
            modifiedHabit.setFrequency(newFrequencyFormatted);

            final String validation = habitValidation.validate(userID, oldHabit, modifiedHabit);
            if (validation == null) {
                habitDataAccessObject.deleteHabit(userID, oldHabit);
                habitDataAccessObject.addHabit(userID, modifiedHabit);

                modifyHabitPresenter.prepareSuccessView(new
                        ModifyHabitOutputData(habitDataAccessObject.fetchHabits(userID)));
            }
            else {
                modifyHabitPresenter.prepareFailView(validation);
            }

        }
        catch (DateTimeParseException dateParseException) {
            modifyHabitPresenter.prepareFailView("Invalid date/time format. "
                    + "Supported formats are: 'dd MMMM, yyyy HH:mm' (e.g., 01 February, 2026 06:00) or "
                    + "'YYYY-MM-DDTHH:MM:SS' (e.g., 2026-02-01T06:00:00).");
        }
        catch (NumberFormatException numberFormatException) {
            modifyHabitPresenter.prepareFailView("Invalid Priority, Streak Count, or "
                    + "Frequency value (must be a number).");
        }
    }

    @Override
    public void switchToHabitListView() {
        modifyHabitPresenter.switchToHabitListView();
    }

}
