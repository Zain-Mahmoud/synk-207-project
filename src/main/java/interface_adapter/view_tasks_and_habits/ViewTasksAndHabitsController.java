package interface_adapter.view_tasks_and_habits;

import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputBoundary;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputData;
import view.ViewTasksAndHabitsView;

import java.util.ArrayList;

/**
 * The controller for the Login Use Case.
 */
public class ViewTasksAndHabitsController {

    private final ViewTasksAndHabitsInputBoundary viewTasksAndHabitsUseCaseInteractor;

    public ViewTasksAndHabitsController(ViewTasksAndHabitsInputBoundary viewTasksAndHabitsUseCaseInteractor) {
        this.viewTasksAndHabitsUseCaseInteractor = viewTasksAndHabitsUseCaseInteractor;
    }

    /**
     * Executes the View Tasks And Habits Use Case.
     */
    public void execute(String designation, int col, String completeableName, Object changedValue) {
        final ViewTasksAndHabitsInputData viewTasksAndHabitsInputData = new ViewTasksAndHabitsInputData(
                designation, col, completeableName, changedValue);

        viewTasksAndHabitsUseCaseInteractor.execute(viewTasksAndHabitsInputData);
    }

    public void exit() {
    }

    public ArrayList<ArrayList<String>> getFormattedTasks() {
        return viewTasksAndHabitsUseCaseInteractor.getFormattedTasks();
    }

    public ArrayList<ArrayList<String>> getFormattedHabits() {
        return viewTasksAndHabitsUseCaseInteractor.getFormattedHabits();
    }
}
