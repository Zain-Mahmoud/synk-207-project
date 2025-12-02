package interface_adapter.view_tasks_and_habits;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputBoundary;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputData;

/**
 * The controller for the View Tasks and Habits Use Case.
 */
public class ViewTasksAndHabitsController {

    private final ViewTasksAndHabitsInputBoundary viewTasksAndHabitsUseCaseInteractor;
    private final LoggedInViewModel loggedInViewModel;

    public ViewTasksAndHabitsController(ViewTasksAndHabitsInputBoundary viewTasksAndHabitsUseCaseInteractor,
                                        LoggedInViewModel loggedInViewModel) {
        this.viewTasksAndHabitsUseCaseInteractor = viewTasksAndHabitsUseCaseInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    /**
     * Calls the respective method in the interactor to execute the use case.
     */
    public void getFormattedTasksAndHabits() {
        final ViewTasksAndHabitsInputData inputData = new ViewTasksAndHabitsInputData(this.loggedInViewModel.getState()
                .getUsername());
        viewTasksAndHabitsUseCaseInteractor.getFormattedTasksAndHabits(inputData);
    }
}
