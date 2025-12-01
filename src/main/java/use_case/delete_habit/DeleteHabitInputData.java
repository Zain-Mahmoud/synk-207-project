package use_case.delete_habit;

public class DeleteHabitInputData {
    private final String username;
    private final String habitName;

    public DeleteHabitInputData(String username, String habitName) {
        this.username = username;
        this.habitName = habitName;
    }

    public String getUsername() {
        return username;
    }

    public String getHabitName() {
        return habitName;
    }
}
