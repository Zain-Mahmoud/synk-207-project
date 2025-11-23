package entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Habit implements Completable {
    private String habitName;
    private LocalDateTime startDateTime;
    private int frequencyCount;
    private String frequencyUnit;
    private String habitGroup;
    private int streakCount;
    private int priority;
    private boolean status;

    Habit(String habitName,
          LocalDateTime startDateTime,
          int frequencyCount,
          String frequencyUnit,
          String habitGroup,
          int streakCount,
          int priority,
          boolean status) {
        this.habitName = habitName;
        this.startDateTime = startDateTime;
        this.frequencyCount = frequencyCount;
        this.frequencyUnit = frequencyUnit;
        this.habitGroup = habitGroup;
        this.streakCount = streakCount;
        this.priority = priority;
        this.status = status;
    }

    @Override
    public void complete() { this.status = true; }

    @Override
    public boolean isCompleted() { return status; }

    public void incrementStreak() { this.streakCount++; }

    public String getHabitName() { return this.habitName; }

    public void setHabitName(String habitName) { this.habitName = habitName; }

    public LocalDateTime getStartDateTime() { return this.startDateTime; }

    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public int getFrequencyCount() { return frequencyCount; }

    public void setFrequencyCount(int frequencyCount) { this.frequencyCount = frequencyCount; }

    public String getFrequencyUnit() { return frequencyUnit; }

    public void setFrequencyUnit(String frequencyUnit) { this.frequencyUnit = frequencyUnit; }
    public String getHabitGroup() { return this.habitGroup; }
    public void setHabitGroup(String habitGroup) { this.habitGroup = habitGroup; }

    public int getStreakCount() { return this.streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }

    public int getPriority() { return this.priority; }
    public void setPriority(int priority) { this.priority = priority; }

    // Implement by default, same convention from other entity.
    // Not necessary, logically being covered by isCompleted.
    public boolean getStatus() { return this.status; }
    public void setStatus( boolean status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Habit)) return false;
        Habit habit = (Habit) o;
        return Objects.equals(habit.habitName, habitName) && Objects.equals(habit.startDateTime, startDateTime);
    }

    @Override
    public String toString() {
        return "Habit{" +
                "habitName='" + habitName + '\'' +
                ", startDateTime=" + startDateTime +
                ", frequencyCount=" + frequencyCount +
                ", frequencyUnit='" + frequencyUnit + '\'' +
                ", habitGroup='" + habitGroup + '\'' +
                ", streakCount=" + streakCount +
                ", priority=" + priority +
                ", status=" + status +
                '}';
    }
}
