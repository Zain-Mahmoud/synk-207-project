package interface_adapter.delete_habit;

import interface_adapter.ViewManagerModel;
import use_case.delete_habit.DeleteHabitOutputBoundary;
import use_case.delete_habit.DeleteHabitOutputData;

public class DeleteHabitPresenter implements DeleteHabitOutputBoundary {

    private final DeleteHabitViewModel deleteHabitViewModel;
    private final ViewManagerModel viewManagerModel;

    public DeleteHabitPresenter(DeleteHabitViewModel deleteHabitViewModel,
                                ViewManagerModel viewManagerModel) {
        this.deleteHabitViewModel = deleteHabitViewModel;
        this.viewManagerModel = viewManagerModel;
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

        //
        // viewManagerModel.setActiveView("SomeOtherViewName");
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
