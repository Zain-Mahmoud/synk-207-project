package interface_adapter.create_habit;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import use_case.create_habit.CreateHabitOutputBoundary;
import use_case.create_habit.CreateHabitOutputData;

public class CreateHabitPresenter implements CreateHabitOutputBoundary {

    private final CreateHabitViewModel createHabitViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;
    private final ViewTasksAndHabitsController viewTasksAndHabitsController;

    public CreateHabitPresenter(CreateHabitViewModel createHabitViewModel,
                                LoggedInViewModel loggedInViewModel,
                                ViewManagerModel viewManagerModel, ViewTasksAndHabitsController viewTasksAndHabitsController) {
        this.createHabitViewModel = createHabitViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
    }

    @Override
    public void prepareSuccessView(CreateHabitOutputData outputData) {
        final CreateHabitState state = createHabitViewModel.getState();

        state.setHabitName("");
        state.setStartDateTimeText("");
        state.setFrequencyText("");
        state.setHabitGroup("");
        state.setStreakCount(0);
        state.setPriority(0);
        state.setStatus(false);

        state.setErrorMessage(null);
        state.setSuccessMessage(
                "Habit '" + outputData.getHabitName() + "' created successfully!");

        createHabitViewModel.setState(state);
        createHabitViewModel.firePropertyChanged();

        if (viewTasksAndHabitsController != null) {
            viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);
        }
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final CreateHabitState state = createHabitViewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setSuccessMessage(null);

        createHabitViewModel.setState(state);
        createHabitViewModel.firePropertyChanged();
    }
}
