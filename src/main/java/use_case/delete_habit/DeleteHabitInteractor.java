package use_case.delete_habit;

import entities.Habit;
import use_case.gateways.HabitGateway;

import java.util.List;
import java.util.Optional;

public class DeleteHabitInteractor implements DeleteHabitInputBoundary {

    private final DeleteHabitOutputBoundary presenter;
    private final HabitGateway habitGateway;

    public DeleteHabitInteractor(DeleteHabitOutputBoundary presenter,
            HabitGateway habitGateway) {
        this.presenter = presenter;
        this.habitGateway = habitGateway;
    }

    @Override
    public void execute(DeleteHabitInputData deleteHabitInputData) {
        String username = deleteHabitInputData.getUsername();
        String habitName = deleteHabitInputData.getHabitName();

        if (habitName == null || habitName.trim().isEmpty()) {
            presenter.prepareFailView("Habit name cannot be empty.");
            return;
        }

        // Fetch all habits for user and find the one with matching name
        List<Habit> habits = habitGateway.getHabitsForUser(username);
        Optional<Habit> habitToDelete = habits.stream()
                .filter(habit -> habit.getName().equals(habitName))
                .findFirst();

        if (habitToDelete.isEmpty()) {
            presenter.prepareFailView(
                    "Habit '" + habitName + "' does not exist for user '" + username + "'.");
            return;
        }

        try {
            habitGateway.deleteHabit(username, habitToDelete.get());

            DeleteHabitOutputData outputData = new DeleteHabitOutputData(username, habitName, false);

            presenter.prepareSuccessView(outputData);

        } catch (Exception exception) {
            presenter.prepareFailView(
                    "Failed to delete habit for user '" + username + "': " + exception.getMessage());
        }
    }
}
