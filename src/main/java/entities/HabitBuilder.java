package entities;

import java.time.LocalDateTime;

public class HabitBuilder {
    private String habitName;
    private LocalDateTime startDateTime;
    private int frequencyCount;
    private String frequencyUnit;
    private String habitGroup;
    private int streakCount = 0;
    private int priority = 0; //waiting for modifying after.
    private boolean status = false; // not sure yet, placeholder.

    public HabitBuilder setHabitName(String habitName) {
        this.habitName = habitName;
        return this;
    }

    public HabitBuilder setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public HabitBuilder setFrequencyCount(int frequencyCount) {
        this.frequencyCount = frequencyCount;
        return this;
    }

    public HabitBuilder setFrequencyUnit(String frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
        return this;
    }


    public HabitBuilder setHabitGroup(String habitGroup) {
        this.habitGroup = habitGroup;
        return this;
    }

    public HabitBuilder setStreakCount(int streakCount) {
        this.streakCount = streakCount;
        return this;
    }

    public HabitBuilder setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public HabitBuilder setStatus(boolean status) {
        this.status = status;
        return this;
    }

    public Habit build() {
        if (habitName == null || habitName.isBlank()) {
            throw new IllegalStateException("habitName must not be null or empty");
        }
        if (startDateTime == null) {
            throw new IllegalStateException("startDateTime must not be null");
        }
        return new Habit(
                habitName,
                startDateTime,
                frequencyCount,
                frequencyUnit,
                habitGroup,
                streakCount,
                priority,
                status
        ); }

}

