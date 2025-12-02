package interface_adapter.view_tasks_and_habits;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputBoundary;

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
     * @param viewModel the view model for the logged in use case
     */
    public void getFormattedTasksAndHabits(LoggedInViewModel viewModel) {
        viewTasksAndHabitsUseCaseInteractor.getFormattedTasksAndHabits(viewModel);
    }
}
