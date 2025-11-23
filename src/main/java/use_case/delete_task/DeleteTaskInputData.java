package use_case.delete_task;

public class DeleteTaskInputData {
    private final String taskName;

    public DeleteTaskInputData(String username, String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() { return taskName; }
}
