package interface_adapter.view_tasks_and_habits;

import java.util.ArrayList;

import interface_adapter.ViewManagerModel;
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
        final ViewTasksAndHabitsState state = viewTasksAndHabitsViewModel.getState();
        final ArrayList<ArrayList<String>> formattedTasks = response.getFormattedTasks();
        final ArrayList<ArrayList<String>> formattedHabits = response.getFormattedHabits();
        state.setFormattedTasks(formattedTasks);
        state.setFormattedHabits(formattedHabits);
        state.setErrorMessage(null);
        viewTasksAndHabitsViewModel.setState(state);
        viewTasksAndHabitsViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final ViewTasksAndHabitsState state = viewTasksAndHabitsViewModel.getState();
        state.setErrorMessage(errorMessage);
        viewTasksAndHabitsViewModel.setState(state);
        viewTasksAndHabitsViewModel.firePropertyChanged();
    }
}
