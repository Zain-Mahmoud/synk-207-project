package interface_adapter.delete_task;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import use_case.delete_task.DeleteTaskOutputBoundary;
import use_case.delete_task.DeleteTaskOutputData;

public class DeleteTaskPresenter implements DeleteTaskOutputBoundary {
    private final DeleteTaskViewModel deleteTaskViewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewTasksAndHabitsController viewTasksAndHabitsController;

    public DeleteTaskPresenter(DeleteTaskViewModel deleteTaskViewModel,
                               ViewManagerModel viewManagerModel, LoggedInViewModel loggedInViewModel, ViewTasksAndHabitsController viewTasksAndHabitsController) {
        this.deleteTaskViewModel = deleteTaskViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
    }

    @Override
    public void prepareSuccessView(DeleteTaskOutputData outputData) {
        final DeleteTaskState deleteTaskState = deleteTaskViewModel.getState();

        deleteTaskState.setUsername(outputData.getUsername());
        deleteTaskState.setTaskName(outputData.getTaskName());
        deleteTaskState.setErrorMessage(null);
        deleteTaskState.setSuccessMessage(
                "Task '" + outputData.getTaskName()
                        + "' deleted successfully for user '" + outputData.getUsername() + "'."
        );

        deleteTaskViewModel.setState(deleteTaskState);
        deleteTaskViewModel.firePropertyChanged();
        if (viewTasksAndHabitsController != null) {
            viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);
        }
    }


    @Override
    public void prepareFailView(String errorMessage) {
        final DeleteTaskState deleteTaskState = deleteTaskViewModel.getState();
        deleteTaskState.setErrorMessage(errorMessage);
        deleteTaskState.setSuccessMessage(null);
        deleteTaskViewModel.setState(deleteTaskState);
        deleteTaskViewModel.firePropertyChanged();
    }
}
