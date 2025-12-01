package use_case.delete_habit;

public class DeleteHabitOutputData {
    private final String username;
    private final String habitName;
    private final boolean useCaseFailed;

    public DeleteHabitOutputData(String username, String habitName, boolean useCaseFailed) {
        this.username = username;
        this.habitName = habitName;
        this.useCaseFailed = useCaseFailed;
    }

    public String getUsername() {
        return username;
    }

    public String getHabitName() {
        return habitName;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}
