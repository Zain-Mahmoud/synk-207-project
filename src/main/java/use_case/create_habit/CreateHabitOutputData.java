package use_case.create_habit;
import java.time.LocalDateTime;

public class CreateHabitOutputData {
    private final String habitName;
    private final LocalDateTime startDateTime;
    private final boolean useCaseFailed;

    public CreateHabitOutputData(String habitName, LocalDateTime startDateTime, boolean useCaseFailed) {
        this.habitName = habitName;
        this.startDateTime = startDateTime;
        this.useCaseFailed = useCaseFailed;
    }

    public String getHabitName() {
        return habitName;
    }
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}
