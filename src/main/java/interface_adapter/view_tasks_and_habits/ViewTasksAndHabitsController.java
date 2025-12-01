package interface_adapter.view_tasks_and_habits;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsInputBoundary;

public class ViewTasksAndHabitsController {

    private final ViewTasksAndHabitsInputBoundary viewTasksAndHabitsUseCaseInteractor;
    private final LoggedInViewModel loggedInViewModel;

    public ViewTasksAndHabitsController(ViewTasksAndHabitsInputBoundary viewTasksAndHabitsUseCaseInteractor,
                                        LoggedInViewModel loggedInViewModel) {
        this.viewTasksAndHabitsUseCaseInteractor = viewTasksAndHabitsUseCaseInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    /**
     * Calls the respective method in the interactor.
     * @param viewModel the View Model for the Logged In use case
     */
    public void getFormattedTasksAndHabits(LoggedInViewModel viewModel) {
        viewTasksAndHabitsUseCaseInteractor.getFormattedTasksAndHabits(viewModel);
    }
}
