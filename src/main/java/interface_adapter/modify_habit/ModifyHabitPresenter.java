package interface_adapter.modify_habit;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginViewModel;
import use_case.modify_habit.ModifyHabitInteractor;
import use_case.modify_habit.ModifyHabitOutputBoundary;
import use_case.modify_habit.ModifyHabitOutputData;


public class ModifyHabitPresenter implements ModifyHabitOutputBoundary {
    private ViewManagerModel viewManagerModel;
    private ModifyHabitViewModel modifyHabitViewModel;
    // private ViewTasksAndHabitsViewModel habitsViewModel;  TODO uncomment when ready



    public ModifyHabitPresenter(ViewManagerModel viewManagerModel,
                                ModifyHabitViewModel modifyHabitViewModel,
                                LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.modifyHabitViewModel = modifyHabitViewModel;
    }

    @Override
    public void prepareSuccessView(ModifyHabitOutputData outputData) {
        // ViewTasksAndHabitsState currState habitsViewModel.getState();
        // currState.setHabitsList(outputData.getHabitsList());
        // habitsViewModel.firePropertyChanged();

        // switchToHabitListView();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewManagerModel.setState(modifyHabitViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    public void switchToHabitListView(){
        // TODO Arya's use case's view

//        viewManagerModel.setState(habitViewModel.getViewName());
//        viewManagerModel.firePropertyChanged();
    }
}
