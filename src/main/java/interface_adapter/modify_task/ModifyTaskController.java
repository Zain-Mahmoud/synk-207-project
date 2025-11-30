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
     * Executes modify task use case.
     * @param oldTaskName old task name
     * @param oldPriority old priority
     * @param oldDeadline old deadline
     * @param oldStatus old status
     * @param oldTaskGroup old task group
     * @param oldStartDateTime old start date time
     * @param oldDescription old description
     * @param newTaskName new task name
     * @param newPriority new priority
     * @param newStartDateTime new start date time
     * @param newDeadline new deadline
     * @param newStatus new status
     * @param newTaskGroup new task group
     * @param newDescription new description
     */
    public void execute(String oldTaskName, String oldPriority, String oldDeadline, String oldStartDateTime,
                        boolean oldStatus, String oldTaskGroup, String oldDescription,
                        String newTaskName, String newPriority, String newDeadline,
                        String newStartDateTime, boolean newStatus, String newTaskGroup, String newDescription) {

        final String username = loggedInViewModel.getState().getUsername();
        final ModifyTaskInputData modifyTaskInputData = new ModifyTaskInputData(oldTaskName, oldPriority,
                oldDeadline, oldStartDateTime, oldStatus,
                oldTaskGroup, oldDescription,
            newTaskName, newPriority, newDeadline, newStartDateTime, newStatus,
                newTaskGroup, newDescription,
                username);

        this.modifyTaskUseCaseInteractor.execute(modifyTaskInputData);

    }

    /**
     * Switches to task list view.
     */
    public void switchToTaskListView() {
        this.modifyTaskUseCaseInteractor.switchToTaskListView();
    }

}
