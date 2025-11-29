package interface_adapter.delete_habit;

public class DeleteHabitState {

    private String username;
    private String habitName;

    private String successMessage;
    private String errorMessage;

    public DeleteHabitState() {
    }

    public DeleteHabitState(DeleteHabitState copy) {
        this.username = copy.username;
        this.habitName = copy.habitName;
        this.successMessage = copy.successMessage;
        this.errorMessage = copy.errorMessage;
    }

    // username

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // habitName

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    // successMessage

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    // errorMessage

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
