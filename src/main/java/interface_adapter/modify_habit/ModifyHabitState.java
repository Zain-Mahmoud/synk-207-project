package interface_adapter.modify_habit;

import java.time.LocalDateTime;

public class ModifyHabitState {
    private String habitName = "";
    private LocalDateTime startDateTime = null;
    private LocalDateTime frequency = null;
    private String habitGroup = "";
    private int streakCount = 0;
    private int priority = 0;
    private boolean status = false;
    private String description = "";

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getFrequency() {
        return frequency;
    }

    public void setFrequency(LocalDateTime frequency) {
        this.frequency = frequency;
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
}
