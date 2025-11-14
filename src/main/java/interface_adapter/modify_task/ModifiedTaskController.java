package interface_adapter.modify_task;

import use_case.modify_task.ModifyTaskInputBoundary;
import use_case.modify_task.ModifyTaskInputData;

import java.time.LocalDateTime;

public class ModifiedTaskController {
    private final ModifyTaskInputBoundary modifyTaskUseCaseInteractor;

    public ModifiedTaskController(ModifyTaskInputBoundary modifyTaskInteractor) {
        this.modifyTaskUseCaseInteractor = modifyTaskInteractor;
    }

    /***
     * Executes modify task use case
     * @param newTaskName
     * @param newPriority
     * @param newDeadline
     * @param newStatus
     */
    public void execute(String newTaskName, int newPriority, LocalDateTime newDeadline, boolean newStatus){
        ModifyTaskInputData modifyTaskInputData = new ModifyTaskInputData(newTaskName, newPriority, newDeadline, newStatus);

        this.modifyTaskUseCaseInteractor.execute(modifyTaskInputData);

    }

}
