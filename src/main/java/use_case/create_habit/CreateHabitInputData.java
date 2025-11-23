package use_case.create_habit;

import java.time.LocalDateTime;

public class CreateHabitInputData {

    private final String username;
    private final String habitName;
    private final LocalDateTime startDateTime;
    private final int frequencyCount;
    private final String frequencyUnit;
    private final String habitGroup;
    private final int streakCount;
    private final int priority;

    public CreateHabitInputData(String username,
                                String habitName,
                                LocalDateTime startDateTime,
                                int frequencyCount,
                                String frequencyUnit,
                                String habitGroup,
                                int streakCount,
                                int priority) {
        this.username = username;
        this.habitName = habitName;
        this.startDateTime = startDateTime;
        this.frequencyCount = frequencyCount;
        this.frequencyUnit = frequencyUnit;
        this.habitGroup = habitGroup;
        this.streakCount = streakCount;
        this.priority = priority;
    }

    public String getUsername() { return username; }
    public String getHabitName() { return habitName; }
    public LocalDateTime getStartDateTime() { return startDateTime; }

    public int getFrequencyCount() { return frequencyCount; }
    public String getFrequencyUnit() { return frequencyUnit; }

    public String getHabitGroup() { return habitGroup; }
    public int getStreakCount() { return streakCount; }
    public int getPriority() { return priority; }
}
