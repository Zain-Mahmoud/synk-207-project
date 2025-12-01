package interface_adapter.create_task;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import use_case.create_task.CreateTaskOutputBoundary;
import use_case.create_task.CreateTaskOutputData;

public class CreateTaskPresenter implements CreateTaskOutputBoundary  {
    private final CreateTaskViewModel createTaskViewModel;
    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;
    private final ViewTasksAndHabitsController viewTasksAndHabitsController;

    public CreateTaskPresenter(CreateTaskViewModel createTaskViewModel,
                               LoggedInViewModel loggedInViewModel,
                               ViewManagerModel viewManagerModel,
                               ViewTasksAndHabitsController viewTasksAndHabitsController) {
        this.createTaskViewModel = createTaskViewModel;
        this.loggedInViewModel = loggedInViewModel;
        this.viewManagerModel = viewManagerModel;
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
    }

    @Override
    public void prepareSuccessView(CreateTaskOutputData outputData) {
        final CreateTaskState createTaskState = createTaskViewModel.getState();
        createTaskState.setUsername("");
        createTaskState.setTaskName("");
        createTaskState.setDescription("");
        createTaskState.setDeadline(null);
        createTaskState.setTaskGroup("");
        createTaskState.setPriority(0);
        createTaskState.setErrorMessage(null);
        createTaskState.setSuccessMessage(
                "Task '" + outputData.getTaskName() +
                        "' created successfully for user '" + outputData.getUsername() + "'!"
        );

        createTaskViewModel.setState(createTaskState);
        createTaskViewModel.firePropertyChanged();

        if (viewTasksAndHabitsController != null) {
            viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);
        }
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final CreateTaskState createTaskState = createTaskViewModel.getState();
        createTaskState.setErrorMessage(errorMessage);
        createTaskState.setSuccessMessage(null);

        createTaskViewModel.setState(createTaskState);
        createTaskViewModel.firePropertyChanged();
    }
}
