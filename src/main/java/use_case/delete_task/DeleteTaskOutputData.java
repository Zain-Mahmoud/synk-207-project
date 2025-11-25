package use_case.delete_task;

public class DeleteTaskOutputData {
    private final String username;
    private final String taskName;
    private final boolean useCaseFailed;

    public DeleteTaskOutputData(String username, String taskName, boolean useCaseFailed) {
        this.username = username;
        this.taskName = taskName;
        this.useCaseFailed = useCaseFailed;
    }

    public String getUsername() {
        return username;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}
