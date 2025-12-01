package interface_adapter.delete_habit;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import use_case.delete_habit.DeleteHabitOutputBoundary;
import use_case.delete_habit.DeleteHabitOutputData;

public class DeleteHabitPresenter implements DeleteHabitOutputBoundary {

    private final DeleteHabitViewModel deleteHabitViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewTasksAndHabitsController viewTasksAndHabitsController;

    public DeleteHabitPresenter(DeleteHabitViewModel deleteHabitViewModel,
                                ViewManagerModel viewManagerModel, LoggedInViewModel loggedInViewModel, ViewTasksAndHabitsController viewTasksAndHabitsController) {
        this.deleteHabitViewModel = deleteHabitViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
    }

    @Override
    public void prepareSuccessView(DeleteHabitOutputData outputData) {
        DeleteHabitState deleteHabitState = deleteHabitViewModel.getState();

        deleteHabitState.setUsername(outputData.getUsername());
        deleteHabitState.setHabitName(outputData.getHabitName());

        deleteHabitState.setErrorMessage(null);
        deleteHabitState.setSuccessMessage(
                "Habit '" + outputData.getHabitName()
                        + "' deleted successfully for user '" + outputData.getUsername() + "'.");

        deleteHabitViewModel.setState(deleteHabitState);
        deleteHabitViewModel.firePropertyChanged();

        if (viewTasksAndHabitsController != null && loggedInViewModel != null) {
            viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);
        }
    }

    @Override
    public void prepareFailView(String errorMessage) {
        DeleteHabitState deleteHabitState = deleteHabitViewModel.getState();
        deleteHabitState.setErrorMessage(errorMessage);
        deleteHabitState.setSuccessMessage(null);

        deleteHabitViewModel.setState(deleteHabitState);
        deleteHabitViewModel.firePropertyChanged();
    }
}
