package use_case.delete_task;

import entities.Task;
import use_case.gateways.TaskGateway;

import java.util.List;
import java.util.Optional;

public class DeleteTaskInteractor implements DeleteTaskInputBoundary {
    private final DeleteTaskOutputBoundary presenter;
    private final TaskGateway taskGateway;

    public DeleteTaskInteractor(DeleteTaskOutputBoundary presenter,
            TaskGateway taskGateway) {
        this.presenter = presenter;
        this.taskGateway = taskGateway;
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

        // Fetch all tasks for user and find the one with matching name
        List<Task> tasks = taskGateway.fetchTasks(username);
        Optional<Task> taskToDelete = tasks.stream()
                .filter(task -> task.getName().equals(taskName))
                .findFirst();

        if (taskToDelete.isEmpty()) {
            presenter.prepareFailView(
                    "Task '" + taskName + "' does not exist for user '" + username + "'.");
            return;
        }

        try {
            taskGateway.deleteTask(username, taskToDelete.get());
            final DeleteTaskOutputData outputData = new DeleteTaskOutputData(username, taskName, false);
            presenter.prepareSuccessView(outputData);
        } catch (Exception exception) {
            presenter.prepareFailView(
                    "Failed to delete task: " + exception.getMessage());
        }
    }
}
