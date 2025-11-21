package interface_adapter.modify_task;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.modify_task.ModifyTaskInputBoundary;
import use_case.modify_task.ModifyTaskInputData;

import java.time.LocalDateTime;

public class ModifyTaskController {
    private final ModifyTaskInputBoundary modifyTaskUseCaseInteractor;
    private final LoggedInViewModel loggedInViewModel;

    public ModifyTaskController(ModifyTaskInputBoundary modifyTaskInteractor, LoggedInViewModel loggedInViewModel) {
        this.modifyTaskUseCaseInteractor = modifyTaskInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    /***
     * Executes modify task use case
     * @param newTaskName
     * @param newPriority
     * @param newDeadline
     * @param newStatus
     */
    public void execute(String newTaskName, int newPriority, LocalDateTime newDeadline, boolean newStatus){

        String username = loggedInViewModel.getState().getUsername();
        ModifyTaskInputData modifyTaskInputData = new ModifyTaskInputData(newTaskName, newPriority, newDeadline, newStatus, username);

        this.modifyTaskUseCaseInteractor.execute(modifyTaskInputData);

    }

    public void switchToTaskListView(){
        this.modifyTaskUseCaseInteractor.switchToTaskListView();
    }

}
