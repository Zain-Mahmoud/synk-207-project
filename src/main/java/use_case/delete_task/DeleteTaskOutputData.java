package use_case.delete_task;

public class DeleteTaskOutputData {
    private final String taskName;
    private final boolean useCaseFailed;


    public DeleteTaskOutputData(String taskName, boolean useCaseFailed) {
        this.taskName = taskName;
        this.useCaseFailed = useCaseFailed;
    }

    public String getTaskName() { return taskName; }

    public boolean isUseCaseFailed() { return useCaseFailed; }
}
