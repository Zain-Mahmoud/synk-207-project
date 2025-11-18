package interface_adapter.create_task;

import use_case.create_task.CreateTaskInputBoundary;
import use_case.create_task.CreateTaskInputData;
import java.time.LocalDateTime;

public class CreateTaskController {
    private final CreateTaskInputBoundary interactor;

    public CreateTaskController(CreateTaskInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void excute(String username, String taskName, LocalDateTime deadline, String group, boolean status, int priority) {
        final CreateTaskInputData createTaskInputData = new CreateTaskInputData(
                username, taskName, deadline, group, status, priority);

        interactor.execute(new CreateTaskInputData(username, taskName, deadline, group, status, priority));
    }
}
