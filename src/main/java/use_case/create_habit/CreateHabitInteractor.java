package use_case.create_habit;

import entities.Habit;
import entities.HabitBuilder;
import use_case.gateways.HabitGateway;

import java.util.List;

public class CreateHabitInteractor implements CreateHabitInputBoundary {
    private final HabitGateway habitGateway;
    private final CreateHabitOutputBoundary createHabitPresenter;

    public CreateHabitInteractor(HabitGateway habitGateway, CreateHabitOutputBoundary createHabitPresenter) {
        this.habitGateway = habitGateway;
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

        // Check if habit exists by fetching all habits and searching by name
        List<Habit> existingHabits = habitGateway.getHabitsForUser(username);
        boolean habitExists = existingHabits.stream()
                .anyMatch(habit -> habit.getName().equals(habitName));

        if (habitExists) {
            createHabitPresenter.prepareFailView(
                    "Habit with name '" + habitName + "' already exists.");
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

            habitGateway.addHabit(username, newHabit);
            final CreateHabitOutputData outputData = new CreateHabitOutputData(
                    newHabit.getName(),
                    newHabit.getStartTime(),
                    false);

            createHabitPresenter.prepareSuccessView(outputData);
        } catch (IllegalStateException exception) {
            createHabitPresenter.prepareFailView("Failed to create habit: " + exception.getMessage());

        } catch (Exception exception) {
            createHabitPresenter.prepareFailView(
                    "Failed to create habit: " + exception.getMessage());
        }
    }
}
