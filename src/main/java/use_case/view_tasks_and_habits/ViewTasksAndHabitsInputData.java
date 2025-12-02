package use_case.view_tasks_and_habits;

/**
 * The Input Data for the Create Task Use Case (empty because this use case takes no input).
 */

public class ViewTasksAndHabitsInputData {
    private String username;

    public ViewTasksAndHabitsInputData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
