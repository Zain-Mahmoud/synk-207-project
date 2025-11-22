package interface_adapter.modify_task;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginViewModel;
import use_case.modify_task.ModifyTaskOutputBoundary;
import use_case.modify_task.ModifyTaskOutputData;

public class ModifyTaskPresenter implements ModifyTaskOutputBoundary {
    private ViewManagerModel viewManagerModel;
    private ModifyTaskViewModel modifyTaskViewModel;
    private LoginViewModel loginViewModel;
    // TODO add Arya's view


    // TODO add Arya's use case's view model when ready
    public ModifyTaskPresenter(ViewManagerModel viewManagerModel,
                               ModifyTaskViewModel modifyTaskViewModel,
                               LoginViewModel loginViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.modifyTaskViewModel = modifyTaskViewModel;
        this.loginViewModel = loginViewModel;
    }

    @Override
    public void prepareSuccessView() {
        // on success, switch to task list view
        // TODO switch to Arya's use case's view when ready
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final ModifyTaskState modifyTaskState = modifyTaskViewModel.getState();
        modifyTaskState.setTaskError(errorMessage);
        modifyTaskViewModel.firePropertyChanged();
    }

    public void switchToTaskListView(){
        // TODO Arya's use case's view
        viewManagerModel.setState(loginViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }
}
