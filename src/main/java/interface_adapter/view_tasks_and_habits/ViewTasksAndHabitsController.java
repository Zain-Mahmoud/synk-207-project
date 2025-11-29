package interface_adapter.view_tasks_and_habits;


import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputBoundary;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputData;
import view.LoggedInView;
import view.ViewTasksAndHabitsView;

import interface_adapter.logged_in.LoggedInViewModel;

import java.util.ArrayList;

/**
 * The controller for the View Tasks and Habits Use Case.
 */
public class ViewTasksAndHabitsController {

    private final ViewTasksAndHabitsInputBoundary viewTasksAndHabitsUseCaseInteractor;
    private final LoggedInViewModel loggedInViewModel;

    public ViewTasksAndHabitsController(ViewTasksAndHabitsInputBoundary viewTasksAndHabitsUseCaseInteractor, LoggedInViewModel loggedInViewModel) {
        this.viewTasksAndHabitsUseCaseInteractor = viewTasksAndHabitsUseCaseInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    /**
     * Executes the View Tasks And Habits Use Case.
     */
    public void execute(String designation, int col, String completeableName, Object changedValue) {
        final ViewTasksAndHabitsInputData viewTasksAndHabitsInputData = new ViewTasksAndHabitsInputData(
                designation, col, completeableName, changedValue);

        viewTasksAndHabitsUseCaseInteractor.execute(viewTasksAndHabitsInputData);
    }

    public void getFormattedTasksAndHabits(LoggedInViewModel loggedInViewModel) {
        viewTasksAndHabitsUseCaseInteractor.getFormattedTasksAndHabits(loggedInViewModel);
    }
}
