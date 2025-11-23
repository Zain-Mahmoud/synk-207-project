package interface_adapter.delete_habit;

public class DeleteHabitState {

    private String username;
    private String habitName;

    private String successMessage;
    private String errorMessage;

    public DeleteHabitState(DeleteHabitState copy) {
        this.username = copy.username;
        this.habitName = copy.habitName;
        this.successMessage = copy.successMessage;
        this.errorMessage = copy.errorMessage;
    }

    public DeleteHabitState() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
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
