package interface_adapter.delete_habit;

import interface_adapter.ViewManagerModel;
import use_case.delete_habit.DeleteHabitOutputBoundary;
import use_case.delete_habit.DeleteHabitOutputData;

public class DeleteHabitPresenter implements DeleteHabitOutputBoundary {
    private final DeleteHabitViewModel deleteHabitViewModel;
    private final ViewManagerModel viewManagerModel;

    public DeleteHabitPresenter(DeleteHabitViewModel deleteHabitViewModel, ViewManagerModel viewManagerModel) {
        this.deleteHabitViewModel = deleteHabitViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(DeleteHabitOutputData outputData) {
        final DeleteHabitState deleteHabitState = deleteHabitViewModel.getState();
        deleteHabitState.setSuccessMessage("Habit '" + outputData.getHabitName() + "' deleted successfully!");
        deleteHabitState.setErrorMessage(null);
        deleteHabitViewModel.setState(deleteHabitState);
        deleteHabitViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final DeleteHabitState deleteHabitState = deleteHabitViewModel.getState();
        deleteHabitState.setErrorMessage(errorMessage);
        deleteHabitState.setSuccessMessage(null);
        deleteHabitViewModel.setState(deleteHabitState);
        deleteHabitViewModel.firePropertyChanged();
    }
}
