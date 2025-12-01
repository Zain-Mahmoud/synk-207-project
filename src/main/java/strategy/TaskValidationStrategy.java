package strategy;

import entities.Task;
import use_case.gateways.TaskGateway;
import java.util.ArrayList;

/**
 * Concrete Strategy: Checks that a modified task's name does not conflict
 * with any other existing task for the same user.
 */
public class TaskValidationStrategy implements ValidationStrategy<Task> {

    private final TaskGateway taskDataAccessObject;

    public TaskValidationStrategy(TaskGateway taskDataAccessObject) {
        this.taskDataAccessObject = taskDataAccessObject;
    }

    @Override
    public String validate(String userID, Task oldTask, Task modifiedTask) {
        if (oldTask == null || modifiedTask == null) {
            return "Cannot validate: Task entities are null.";
        }

        ArrayList<Task> taskList = taskDataAccessObject.fetchTasks(userID);

        for (Task task : taskList) {
            if (!task.getName().equals(oldTask.getName()) && task.getName().equals(modifiedTask.getName())) {
                return "Task already exists";
            }
        }

        return null;
    }
}