package interface_adapter.create_habit;

public class CreateHabitState {

    private String habitName = "";
    private String startDateTimeText = "";
    private String frequencyText = "";
    private String habitGroup = "";
    private int frequencyCount = 1;
    private String frequencyUnit = "Per Day";

    private int streakCount = 0;
    private int priority = 0;
    private boolean status = false;

    private String errorMessage = null;
    private String successMessage = null;

    public CreateHabitState(CreateHabitState copy) {
        this.habitName = copy.habitName;
        this.startDateTimeText = copy.startDateTimeText;
        this.frequencyText = copy.frequencyText;
        this.habitGroup = copy.habitGroup;
        this.streakCount = copy.streakCount;
        this.priority = copy.priority;
        this.status = copy.status;
        this.errorMessage = copy.errorMessage;
        this.successMessage = copy.successMessage;
    }

    public CreateHabitState() {
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getStartDateTimeText() {
        return startDateTimeText;
    }

    public void setStartDateTimeText(String startDateTimeText) {
        this.startDateTimeText = startDateTimeText;
    }

    public String getFrequencyText() {
        return frequencyText;
    }

    public void setFrequencyText(String frequencyText) {
        this.frequencyText = frequencyText;
    }

    public String getHabitGroup() {
        return habitGroup;
    }

    public void setHabitGroup(String habitGroup) {
        this.habitGroup = habitGroup;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}