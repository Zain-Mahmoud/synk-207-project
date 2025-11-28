package interface_adapter.view_tasks_and_habits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The State information for the View Tasks and Habits View.
 */
public class ViewTasksAndHabitsState {
    private ArrayList<ArrayList<String>> formattedTasks = new ArrayList<>();
    private ArrayList<ArrayList<String>> formattedHabits = new ArrayList<>();
    private String errorMessage = null;

    public ViewTasksAndHabitsState() {
    }

    public ViewTasksAndHabitsState(ViewTasksAndHabitsState copy) {
        formattedTasks = copy.formattedTasks;
        formattedHabits = copy.formattedHabits;
        errorMessage = copy.errorMessage;
    }

    public ArrayList<ArrayList<String>> getFormattedTasks() {
        return formattedTasks;
    }

    public void setFormattedTasks(ArrayList<ArrayList<String>> formattedTasks) {
        this.formattedTasks = formattedTasks;
    }

    public ArrayList<ArrayList<String>> getFormattedHabits() {
        return formattedHabits;
    }

    public void setFormattedHabits(ArrayList<ArrayList<String>> formattedHabits) {
        this.formattedHabits = formattedHabits;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

