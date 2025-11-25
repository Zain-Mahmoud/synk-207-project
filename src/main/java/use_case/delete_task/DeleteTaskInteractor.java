package use_case.delete_task;

public class DeleteTaskInteractor implements DeleteTaskInputBoundary {
    private final DeleteTaskOutputBoundary presenter;
    private final DeleteTaskUserDataAccess dao;

    public DeleteTaskInteractor(DeleteTaskOutputBoundary presenter,
                                DeleteTaskUserDataAccess dao) {
        this.presenter = presenter;
        this.dao = dao;
    }

    @Override
    public void execute(DeleteTaskInputData deleteTaskInputData) {
        final String username = deleteTaskInputData.getUsername();
        final String taskName = deleteTaskInputData.getTaskName();

        if (username == null || username.trim().isEmpty()) {
            presenter.prepareFailView("Username cannot be empty.");
            return;
        }

        if (taskName == null || taskName.trim().isEmpty()) {
            presenter.prepareFailView("Task name cannot be empty.");
            return;
        }

        if (!dao.existsTaskByName(username, taskName)) {
            presenter.prepareFailView(
                    "Task '" + taskName + "' does not exist for user '" + username + "'."
            );
            return;
        }

        try {
            dao.deleteTask(username, taskName);
            final DeleteTaskOutputData outputData =
                    new DeleteTaskOutputData(username, taskName, false);
            presenter.prepareSuccessView(outputData);
        } catch (Exception exception) {
            presenter.prepareFailView(
                    "Failed to delete task: " + exception.getMessage()
            );
        }
    }
}
