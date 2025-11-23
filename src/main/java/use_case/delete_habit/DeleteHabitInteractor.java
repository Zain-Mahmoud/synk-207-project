package use_case.delete_habit;

public class DeleteHabitInteractor implements DeleteHabitInputBoundary {

    private final DeleteHabitOutputBoundary presenter;
    private final DeleteHabitUserDataAccess dao;

    public DeleteHabitInteractor(DeleteHabitOutputBoundary presenter,
                                 DeleteHabitUserDataAccess dao) {
        this.presenter = presenter;
        this.dao = dao;
    }

    @Override
    public void execute(DeleteHabitInputData deleteHabitInputData) {
        String username = deleteHabitInputData.getUsername();
        String habitName = deleteHabitInputData.getHabitName();

        if (habitName == null || habitName.trim().isEmpty()) {
            presenter.prepareFailView("Habit name cannot be empty.");
            return;
        }

        if (!dao.existsByName(username, habitName)) {
            presenter.prepareFailView(
                    "Habit '" + habitName + "' does not exist for user '" + username + "'.");
            return;
        }

        try {
            dao.deleteHabit(username, habitName);

            DeleteHabitOutputData outputData =
                    new DeleteHabitOutputData(username, habitName, false);

            presenter.prepareSuccessView(outputData);

        } catch (Exception exception) {
            presenter.prepareFailView(
                    "Failed to delete habit for user '" + username + "': " + exception.getMessage());
        }
    }
}
