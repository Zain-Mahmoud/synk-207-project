package use_case.delete_task;

public class DeleteTaskInteractor implements DeleteTaskInputBoundary {
    private final DeleteTaskOutputBoundary presenter;
    private final DeleteTaskUserDataAccess dao;

    public DeleteTaskInteractor(DeleteTaskOutputBoundary presenter, DeleteTaskUserDataAccess dao) {
        this.presenter = presenter;
        this.dao = dao;
    }


    @Override
    public void excute(DeleteTaskInputData deleteTaskInputData) {
        final String username = deleteTaskInputData.getUsername();
        final String taskName = deleteTaskInputData.getTaskName();

        if (taskName == null || taskName.trim().isEmpty()) {
            presenter.prepareFailView("Task name cannot be empty.");
            return;
        }
        if (!dao.existsTaskByName(username, taskName)) {
            presenter.prepareFailView(
                    "Task '" + taskName + "' does not exist."
            );
            return;
        }

        try {
            dao.deleteTask(username, taskName);
            final DeleteTaskOutputData outputData = new DeleteTaskOutputData(taskName, false);
            presenter.prepareSuccessView(outputData);
        } catch (Exception exception) {
            presenter.prepareFailView("Failed to delete task:" + exception.getMessage());
        }
    }
}
