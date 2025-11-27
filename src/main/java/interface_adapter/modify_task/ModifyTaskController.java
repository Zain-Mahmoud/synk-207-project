package interface_adapter.modify_task;

import interface_adapter.logged_in.LoggedInViewModel;
import use_case.modify_task.ModifyTaskInputBoundary;
import use_case.modify_task.ModifyTaskInputData;



public class ModifyTaskController {
    private final ModifyTaskInputBoundary modifyTaskUseCaseInteractor;
    private final LoggedInViewModel loggedInViewModel;

    public ModifyTaskController(ModifyTaskInputBoundary modifyTaskInteractor, LoggedInViewModel loggedInViewModel) {
        this.modifyTaskUseCaseInteractor = modifyTaskInteractor;
        this.loggedInViewModel = loggedInViewModel;
    }

    /***
     * Executes modify task use case
     * @param oldTaskName
     * @param oldPriority
     * @param oldDeadline
     * @param oldStatus
     * @param oldTaskGroup
     * @param oldDescription
     * @param oldStartTime
     * @param newTaskName
     * @param newPriority
     * @param newDeadline
     * @param newStatus
     * @param newTaskGroup
     * @param newDescription
     * @param newStartTime
     */
    public void execute(String oldTaskName, String oldPriority, String oldDeadline, boolean oldStatus,
                        String oldTaskGroup, String oldDescription, String oldStartTime,
                        String newTaskName, String newPriority, String newDeadline, boolean newStatus,
                        String newTaskGroup, String newDescription, String newStartTime){

        String username = loggedInViewModel.getState().getUsername();
        ModifyTaskInputData modifyTaskInputData = new ModifyTaskInputData(oldTaskName, oldPriority, oldDeadline, oldStatus,
                oldTaskGroup, oldDescription, oldStartTime,
                newTaskName, newPriority, newDeadline, newStatus,
                newTaskGroup, newDescription, newStartTime,
                username);

        this.modifyTaskUseCaseInteractor.execute(modifyTaskInputData);

    }

    public void switchToTaskListView(){
        this.modifyTaskUseCaseInteractor.switchToTaskListView();
    }

}