package use_case.modify_task;

import entities.Task;
import entities.TaskBuilder;
import use_case.gateways.TaskHabitGateway;

import java.time.LocalDateTime;

public class ModifyTaskInteractor implements ModifyTaskInputBoundary {
    private final ModifyTaskOutputBoundary modifyPresenter;
    private final TaskHabitGateway userDataAccessObject;

    public ModifyTaskInteractor(ModifyTaskOutputBoundary modifyPresenter, TaskHabitGateway userDataAccessObject) {
        this.modifyPresenter = modifyPresenter;
        this.userDataAccessObject = userDataAccessObject;
    }

    public void execute(ModifyTaskInputData modifyInputData) {

        String oldTaskName = modifyInputData.getOldTaskName();
        int oldTaskPriority = modifyInputData.getOldPriority();
        boolean oldTaskStatus = modifyInputData.getOldTaskStatus();
        String oldDeadline = modifyInputData.getOldDeadline();
        
        String newTaskName = modifyInputData.getNewTaskName();
        String newTaskPriority = modifyInputData.getNewPriority();
        boolean newTaskStatus = modifyInputData.getNewTaskStatus();
        String newDeadline = modifyInputData.getNewDeadline();
        String userID = modifyInputData.getUserID();

        try{
            LocalDateTime newDeadlineFormatted = LocalDateTime.parse(newDeadline);
            int newPriorityFormatted = Integer.parseInt(newTaskPriority);
            final Task modifiedTask = new TaskBuilder()
                    .setTaskName(newTaskName)
                    .setDeadline(newDeadlineFormatted)
                    .setPriority(newPriorityFormatted)
                    .setStatus(newTaskStatus).build();

            final Task oldTask = new TaskBuilder()
                    .setTaskName(oldTaskName)
                    .setDeadline(LocalDateTime.parse(oldDeadline))
                    .setPriority(oldTaskPriority)
                    .setStatus(oldTaskStatus).build();

            userDataAccessObject.deleteTask(userID, oldTask);
            userDataAccessObject.addTask(userID, modifiedTask);


            modifyPresenter.prepareSuccessView();
        } catch (java.time.format.DateTimeParseException d) {
            modifyPresenter.prepareFailView("Invalid Deadline");
        } catch (NumberFormatException n) {
            modifyPresenter.prepareFailView("Invalid Priority");
        }


        



    }

    @Override
    public void switchToTaskListView() {
        modifyPresenter.switchToTaskListView();
    }
}

