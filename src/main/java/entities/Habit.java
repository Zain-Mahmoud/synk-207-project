package entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Habit implements Completable, Cloneable {
    private String habitName;
    private LocalDateTime startDateTime;
    private int frequency;
    private String habitGroup;
    private int streakCount;
    private int priority;
    private boolean status;
    private String description;

    Habit(String habitName,
          LocalDateTime startDateTime,
          int frequency,
          String habitGroup,
          int streakCount,
          int priority,
          boolean status) {
        this.habitName = habitName;
        this.startDateTime = startDateTime;
        this.frequency = frequency;
        this.habitGroup = habitGroup;
        this.streakCount = streakCount;
        this.priority = priority;
        this.status = status;
        this.description = this.habitName + " started on " + this.startDateTime.toString();
    }

    @Override
    public void complete() { this.status = true; }

    @Override
    public boolean isCompleted() { return status; }

    @Override
    public String getName() {
        return this.habitName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * @return
     */
    @Override
    public LocalDateTime getDueDate() {
        return this.getDueDate();
    }


    public void incrementStreak() { this.streakCount++; }


    public void setHabitName(String habitName) { this.habitName = habitName; }

    public LocalDateTime getStartTime() { return this.startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public int getFrequency() { return this.frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    public String getHabitGroup() { return this.habitGroup; }
    public void setHabitGroup(String habitGroup) { this.habitGroup = habitGroup; }

    public int getStreakCount() { return this.streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }

    public int getPriority() { return this.priority; }



    public void setPriority(int priority) { this.priority = priority; }


    public boolean getStatus() { return this.status; }
    public void setStatus( boolean status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Habit)) return false;
        Habit habit = (Habit) o;
        return Objects.equals(habit.habitName, habitName) && Objects.equals(habit.startDateTime, startDateTime) &&
                Objects.equals(habit.frequency, frequency) && Objects.equals(habit.habitGroup, habitGroup) &&
                Objects.equals(habit.description, description) && Objects.equals(habit.status, status) &&
                Objects.equals(habit.priority, priority) && Objects.equals(habit.streakCount, streakCount);
    }

    @Override
    public String toString() {
        return "Habit{" +
                "habitName='" + habitName + '\'' +
                ", startDateTime=" + startDateTime +
                ", frequency=" + frequency +
                ", habitGroup='" + habitGroup + '\'' +
                ", streakCount=" + streakCount +
                ", priority=" + priority +
                ", status=" + status +
                '}';
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Habit clone(){
        try{
            return (Habit) super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
}