package use_case.create_task;

public class CreateTaskOutputData {
    private final String taskName;
    private final boolean created;
    private final String message;

    public CreateTaskOutputData(String taskName, boolean created, String message) {
        this.taskName = taskName;
        this.created = created;
        this.message = message;
    }

    public String getTaskName() { return taskName; }
    public boolean isCreated() { return created; }
    public String getMessage() { return message; }
}
