package interface_adapter.delete_task;

public class DeleteTaskState {

    private String username;
    private String successMessage = null;
    private String errorMessage = null;

    public DeleteTaskState(DeleteTaskState copy) {
        this.username = copy.username;
        this.successMessage = copy.successMessage;
        this.errorMessage = copy.errorMessage;
    }

    public DeleteTaskState() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
