package interface_adapter.delete_task;

public class DeleteTaskState {
    private String successMessage = null;
    private String errorMessage = null;

    public DeleteTaskState(DeleteTaskState copy) {
        this.successMessage = copy.successMessage;
        this.errorMessage = copy.errorMessage;
    }
    public DeleteTaskState() {
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
