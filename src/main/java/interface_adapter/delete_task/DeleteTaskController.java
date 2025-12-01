package interface_adapter.delete_task;

import use_case.delete_task.DeleteTaskInputBoundary;
import use_case.delete_task.DeleteTaskInputData;

public class DeleteTaskController {
    private final DeleteTaskInputBoundary interactor;

    public DeleteTaskController(DeleteTaskInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Execute the delete task use case for a given user and task name.
     */
    public void execute(String username, String taskName) {
        final DeleteTaskInputData inputData = new DeleteTaskInputData(username, taskName);
        interactor.execute(inputData);
    }
}
