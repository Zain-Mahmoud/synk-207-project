package interface_adapter.modify_habit;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginViewModel;
import use_case.modify_habit.ModifyHabitOutputBoundary;
import use_case.modify_habit.ModifyHabitOutputData;

public class ModifyHabitPresenter implements ModifyHabitOutputBoundary {
    private ViewManagerModel viewManagerModel;
    private ModifyHabitViewModel modifyHabitViewModel;
    private LoginViewModel loginViewModel;
    // TODO add Arya's view


    // TODO add Arya's use case's view model when ready
    public ModifyHabitPresenter(ViewManagerModel viewManagerModel,
                                ModifyHabitViewModel modifyHabitViewModel,
                                LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.modifyHabitViewModel = modifyHabitViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView(ModifyHabitOutputData outputData) {
        // on success, switch to habit list view
        // TODO switch to Arya's use case's view when ready
    }

    @Override
    public void prepareFailView(String errorMessage) {

    }

    public void switchToHabitListView(){
        // TODO Arya's use case's view
        viewManagerModel.setState(loginViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}
