package interface_adapter.modify_habit;

public class ModifyHabitState {
    private String habitName = "";
    private String startDateTime = "";
    private String frequency = "";
    private String habitGroup = "";
    private String streakCount = "";
    private String priority = "";
    private boolean status = false;
    private String description = "";
    private String habitError;

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getHabitGroup() {
        return habitGroup;
    }

    public void setHabitGroup(String habitGroup) {
        this.habitGroup = habitGroup;
    }

    public String getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(String streakCount) {
        this.streakCount = streakCount;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHabitError(String habitError) {
        this.habitError = habitError;
    }

    public String getHabitError() {
        return habitError;
    }
}
