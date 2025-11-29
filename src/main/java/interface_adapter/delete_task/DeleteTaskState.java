package interface_adapter.delete_task;

public class DeleteTaskState {

    private String username;
    private String taskName;

    private String successMessage = null;
    private String errorMessage = null;

    public DeleteTaskState(DeleteTaskState copy) {
        this.username = copy.username;
        this.taskName = copy.taskName;
        this.successMessage = copy.successMessage;
        this.errorMessage = copy.errorMessage;
    }

    public DeleteTaskState() {
    }

    // --- username ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // --- taskName ---

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    // --- messages ---

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
