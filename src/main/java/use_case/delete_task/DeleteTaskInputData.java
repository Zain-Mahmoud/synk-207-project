package use_case.delete_task;

public class DeleteTaskInputData {
    private final String username;
    private final String taskName;

    public DeleteTaskInputData(String username, String taskName) {
        this.username = username;
        this.taskName = taskName;
    }

    public String getUsername() { return username; }

    public String getTaskName() { return taskName; }
}
