package use_case.create_habit;

import entities.Habit;
import entities.HabitBuilder;

public class CreateHabitInteractor implements CreateHabitInputBoundary {
    private final CreateHabitUserDataAccess habitDataAccessObject;
    private final CreateHabitOutputBoundary createHabitPresenter;

    public CreateHabitInteractor(CreateHabitUserDataAccess habitDataAccessObject, CreateHabitOutputBoundary createHabitPresenter) {
        this.habitDataAccessObject = habitDataAccessObject;
        this.createHabitPresenter = createHabitPresenter;
    }


    @Override
    public void execute(CreateHabitInputData createHabitInputData) {
        final String username = createHabitInputData.getUsername();
        final String habitName = createHabitInputData.getHabitName();

        if (habitName == null || habitName.trim().isEmpty()) {
            createHabitPresenter.prepareFailView("Habit name cannot be empty.");
            return;
        }

        if (habitDataAccessObject.existsByName(username, habitName)) {
            createHabitPresenter.prepareFailView(
                    "Habit with name '" + habitName + "' already exists."
            );
            return;
        }

        try {
            final Habit newHabit = new HabitBuilder()
                    .setHabitName(habitName)
                    .setStartDateTime(createHabitInputData.getStartDateTime())
                    .setFrequency(createHabitInputData.getFrequency())
                    .setHabitGroup(createHabitInputData.getHabitGroup())
                    .setStreakCount(createHabitInputData.getStreakCount())
                    .setPriority(createHabitInputData.getPriority())
                    .build();


            habitDataAccessObject.save(username, newHabit);
            final CreateHabitOutputData outputData = new CreateHabitOutputData(
                    newHabit.getName(),
                    newHabit.getStartDateTime(),
                    false
            );

            createHabitPresenter.prepareSuccessView(outputData);
        } catch (IllegalStateException exception) {
            createHabitPresenter.prepareFailView("Failed to create habit: " + exception.getMessage());

        } catch (Exception exception) {
            createHabitPresenter.prepareFailView(
                    "Failed to create habit: " + exception.getMessage()
            );
        }
}}
