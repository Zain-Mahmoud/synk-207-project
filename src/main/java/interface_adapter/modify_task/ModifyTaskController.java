package interface_adapter.modify_task;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.modify_task.ModifyTaskInputBoundary;
import use_case.modify_task.ModifyTaskInputData;



public class ModifyTaskController {
    private final ModifyTaskInputBoundary modifyTaskUseCaseInteractor;
    private final LoggedInViewModel loggedInViewModel;

    // TODO obtain previous task information from Arya's task list view model
    public ModifyTaskController(ModifyTaskInputBoundary modifyTaskInteractor, LoggedInViewModel loggedInViewModel) {
        this.modifyTaskUseCaseInteractor = modifyTaskInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    // TODO remove previous task parameters from execute method and obtain them from Arya's task list view state
    /***
    /***
     * Executes modify task use case
     * @param oldTaskName
     * @param oldPriority
     * @param oldDeadline
     * @param oldStatus
     * @param newTaskName
     * @param newPriority
     * @param newDeadline
     * @param newStatus
     */
    public void execute(String oldTaskName, int oldPriority, String oldDeadline, boolean oldStatus,
                        String newTaskName, String newPriority, String newDeadline, boolean newStatus){

        String username = loggedInViewModel.getState().getUsername();
        ModifyTaskInputData modifyTaskInputData = new ModifyTaskInputData(oldTaskName, oldPriority, oldDeadline, oldStatus,
                                                                            newTaskName, newPriority, newDeadline, newStatus, 
                                                                            username);

        this.modifyTaskUseCaseInteractor.execute(modifyTaskInputData);

    }

    public void switchToTaskListView(){
        this.modifyTaskUseCaseInteractor.switchToTaskListView();
    }

}
