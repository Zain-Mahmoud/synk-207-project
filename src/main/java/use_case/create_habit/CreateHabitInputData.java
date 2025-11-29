package use_case.create_habit;

import java.time.LocalDateTime;

public class CreateHabitInputData {

    private final String username;
    private final String habitName;
    private final LocalDateTime startDateTime;
    private final int frequency;
    private final String habitGroup;
    private final int streakCount;
    private final int priority;

    public CreateHabitInputData(String username,
            String habitName,
            LocalDateTime startDateTime,
            int frequency,
            String habitGroup,
            int streakCount,
            int priority) {
        this.username = username;
        this.habitName = habitName;
        this.startDateTime = startDateTime;
        this.frequency = frequency;
        this.habitGroup = habitGroup;
        this.streakCount = streakCount;
        this.priority = priority;
    }

    public String getUsername() {
        return username;
    }

    public String getHabitName() {
        return habitName;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getHabitGroup() {
        return habitGroup;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public int getPriority() {
        return priority;
    }
}
