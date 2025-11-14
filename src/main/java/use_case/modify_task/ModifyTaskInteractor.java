package use_case.modify_task;

import entities.Task;
import entities.TaskBuilder;

import java.time.LocalDateTime;

public class ModifyTaskInteractor implements ModifyTaskInputBoundary {
    private final ModifyTaskOutputBoundary modifyPresenter;
    private final ModifyTaskUserDataAccessInterface userDataAccessObject;

    public ModifyTaskInteractor(ModifyTaskOutputBoundary modifyPresenter, ModifyTaskUserDataAccessInterface userDataAccessObject) {
        this.modifyPresenter = modifyPresenter;
        this.userDataAccessObject = userDataAccessObject;
    }

    public void execute(ModifyTaskInputData modifyInputData) {

        String newTaskName = modifyInputData.getNewTaskName();
        int newTaskPriority = modifyInputData.getNewPriority();
        boolean newTaskStatus = modifyInputData.getNewTaskStatus();
        LocalDateTime newDeadline = modifyInputData.getNewDeadline();

        final Task modifiedTask = new TaskBuilder()
                .setTaskName(newTaskName)
                .setDeadline(newDeadline)
                .setPriority(newTaskPriority)
                .setStatus(newTaskStatus).build();
        userDataAccessObject.modifyTask(modifiedTask);
        final ModifyTaskOutputData outputData = new ModifyTaskOutputData(newTaskName, newDeadline, newTaskStatus,
                newTaskPriority, newTaskStatus);
        modifyPresenter.prepareSuccessView(outputData);

    }
}

