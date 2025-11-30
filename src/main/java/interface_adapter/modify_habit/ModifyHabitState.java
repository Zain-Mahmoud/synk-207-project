package interface_adapter.modify_habit;

public class ModifyHabitState {

    private String habitName;
    private String startDateTime;
    private String frequency;
    private String habitGroup;
    private String streakCount;
    private String priority;
    private boolean status;
    private String description;
    private String habitError;

    private String oldHabitName;
    private String oldStartDateTime;
    private String oldFrequency;
    private String oldHabitGroup;
    private String oldStreakCount;
    private String oldPriority;
    private boolean oldStatus;
    private String oldDescription;

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

    public String getHabitError() {
        return habitError;
    }

    public void setHabitError(String habitError) {
        this.habitError = habitError;
    }

    // ========== Old getters & setters ==========

    public String getOldHabitName() {
        return oldHabitName;
    }

    public void setOldHabitName(String oldHabitName) {
        this.oldHabitName = oldHabitName;
    }

    public String getOldStartDateTime() {
        return oldStartDateTime;
    }

    public void setOldStartDateTime(String oldStartDateTime) {
        this.oldStartDateTime = oldStartDateTime;
    }

    public String getOldFrequency() {
        return oldFrequency;
    }

    public void setOldFrequency(String oldFrequency) {
        this.oldFrequency = oldFrequency;
    }

    public String getOldHabitGroup() {
        return oldHabitGroup;
    }

    public void setOldHabitGroup(String oldHabitGroup) {
        this.oldHabitGroup = oldHabitGroup;
    }

    public String getOldStreakCount() {
        return oldStreakCount;
    }

    public void setOldStreakCount(String oldStreakCount) {
        this.oldStreakCount = oldStreakCount;
    }

    public String getOldPriority() {
        return oldPriority;
    }

    public void setOldPriority(String oldPriority) {
        this.oldPriority = oldPriority;
    }

    public boolean getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(boolean oldStatus) {
        this.oldStatus = oldStatus;
    }
}
