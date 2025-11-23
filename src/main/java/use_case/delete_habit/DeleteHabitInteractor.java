package use_case.delete_habit;

public class DeleteHabitInteractor implements DeleteHabitInputBoundary {
    private final DeleteHabitOutputBoundary presenter;
    private final DeleteHabitUserDataAccess dao;

    public DeleteHabitInteractor(DeleteHabitOutputBoundary presenter, DeleteHabitUserDataAccess dao) {
        this.presenter = presenter;
        this.dao = dao;
    }

    @Override
    public void excute(DeleteHabitInputData deleteHabitInputData) {
        final String username = deleteHabitInputData.getUsername();
        final String habitName = deleteHabitInputData.getHabitName();

        if (habitName == null || habitName.trim().isEmpty()) {
            presenter.prepareFailView("Habit name cannot be empty.");
            return;
        }

        if (!dao.existsByName(username, habitName)) {
            presenter.prepareFailView(
                    "Habit '" + habitName + "' does not exist."
            );
            return;
        }

        try {
            dao.deleteHabit(username, habitName);
            final DeleteHabitOutputData outputData = new DeleteHabitOutputData(username, habitName, false);
            presenter.prepareSuccessView(outputData);
        } catch (Exception exception) {
            presenter.prepareFailView("Failed to delete habit: " + exception.getMessage());
        }
    }
}
