package interface_adapter.delete_task;

import interface_adapter.ViewManagerModel;
import use_case.delete_task.DeleteTaskOutputBoundary;
import use_case.delete_task.DeleteTaskOutputData;

public class DeleteTaskPresenter implements DeleteTaskOutputBoundary {
    private final DeleteTaskViewModel deleteTaskViewModel;
    private final ViewManagerModel viewManagerModel;

    public DeleteTaskPresenter(DeleteTaskViewModel deleteTaskViewModel,
                               ViewManagerModel viewManagerModel) {
        this.deleteTaskViewModel = deleteTaskViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(DeleteTaskOutputData outputData) {
        final DeleteTaskState deleteTaskState = deleteTaskViewModel.getState();

        deleteTaskState.setUsername(outputData.getUsername());
        deleteTaskState.setTaskName(outputData.getTaskName());

        deleteTaskState.setSuccessMessage(
                "Task '" + outputData.getTaskName()
                        + "' deleted successfully for user '" + outputData.getUsername() + "'."
        );
        deleteTaskState.setErrorMessage(null);

        deleteTaskViewModel.setState(deleteTaskState);
        deleteTaskViewModel.firePropertyChanged();
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
