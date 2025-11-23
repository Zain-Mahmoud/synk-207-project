package interface_adapter.view_tasks_and_habits;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsOutputBoundary;
import use_case.view_tasks_and_habits.ViewTasksAndHabitsOutputData;

/**
 * The Presenter for the Login Use Case.
 */
public class ViewTasksAndHabitsPresenter implements ViewTasksAndHabitsOutputBoundary {

    private final ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel;
    private final ViewManagerModel viewManagerModel;

    public ViewTasksAndHabitsPresenter(ViewManagerModel viewManagerModel,
                                       ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.viewTasksAndHabitsViewModel = viewTasksAndHabitsViewModel;

    }

    @Override
    public void prepareSuccessView(ViewTasksAndHabitsOutputData response) {
    }

    @Override
    public void prepareFailView(String error) {
    }
}
