package use_case.modify_task;

import entities.Task;
import entities.TaskBuilder;
import use_case.gateways.TaskGateway;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ModifyTaskInteractor implements ModifyTaskInputBoundary {
    private final ModifyTaskOutputBoundary modifyTaskPresenter;
    private final TaskGateway userDataAccessObject;

    public ModifyTaskInteractor(ModifyTaskOutputBoundary modifyTaskPresenter, TaskGateway userDataAccessObject) {
        this.modifyTaskPresenter = modifyTaskPresenter;
        this.userDataAccessObject = userDataAccessObject;
    }

    public void execute(ModifyTaskInputData modifyInputData) {

        String oldTaskName = modifyInputData.getOldTaskName();
        String oldTaskPriority = modifyInputData.getOldPriority();
        boolean oldTaskStatus = modifyInputData.getOldTaskStatus();
        String oldDeadline = modifyInputData.getOldDeadline();
        String oldTaskGroup = modifyInputData.getOldTaskGroup();
        String oldDescription = modifyInputData.getOldDescription();
        String oldStartTimeRaw = modifyInputData.getOldStartTime();

        String newTaskName = modifyInputData.getNewTaskName();
        String newTaskPriority = modifyInputData.getNewPriority();
        boolean newTaskStatus = modifyInputData.getNewTaskStatus();
        String newDeadline = modifyInputData.getNewDeadline();
        String newTaskGroup = modifyInputData.getNewTaskGroup();
        String newDescription = modifyInputData.getNewDescription();
        String newStartTimeRaw = modifyInputData.getNewStartTime();
        String userID = modifyInputData.getUserID();

        try{

            LocalDateTime newDeadlineFormatted = LocalDateTime.parse(newDeadline);
            LocalDateTime newStartTimeFormatted = LocalDateTime.parse(newStartTimeRaw);
            int newPriorityFormatted = Integer.parseInt(newTaskPriority);
            int oldPriorityFormatted = Integer.parseInt(oldTaskPriority);

            if (newDeadlineFormatted.isBefore(LocalDateTime.now())) {
                modifyTaskPresenter.prepareFailView("New deadline cannot be in the past.");
                return;
            }


            final Task modifiedTask = new TaskBuilder()
                    .setTaskName(newTaskName)
                    .setDescription(newDescription)
                    .setDeadline(newDeadlineFormatted)
                    .setStartTime(newStartTimeFormatted)
                    .setTaskGroup(newTaskGroup)
                    .setPriority(newPriorityFormatted)
                    .setStatus(newTaskStatus).build();

            LocalDateTime oldDeadlineFormatted = LocalDateTime.parse(oldDeadline);
            LocalDateTime oldStartTimeFormatted = LocalDateTime.parse(oldStartTimeRaw);

            final Task oldTask = new TaskBuilder()
                    .setTaskName(oldTaskName)
                    .setDescription(oldDescription)
                    .setDeadline(oldDeadlineFormatted)
                    .setStartTime(oldStartTimeFormatted) // ADDED setStartTime
                    .setTaskGroup(oldTaskGroup)
                    .setPriority(oldPriorityFormatted)
                    .setStatus(oldTaskStatus).build();

            ArrayList<Task> taskList = userDataAccessObject.fetchTasks(userID);

            for (Task task : taskList) {
                if (!task.equals(oldTask) && task.getName().equals(modifiedTask.getName())){
                    modifyTaskPresenter.prepareFailView("Task already exists");
                    return;
                }
            }

            userDataAccessObject.deleteTask(userID, oldTask);
            userDataAccessObject.addTask(userID, modifiedTask);


            modifyTaskPresenter.prepareSuccessView(new ModifyTaskOutputData(userDataAccessObject.fetchTasks(userID)));
        } catch (java.time.format.DateTimeParseException d) {
            modifyTaskPresenter.prepareFailView("Invalid date/time format. Use ISO format (e.g., YYYY-MM-DDTHH:MM:SS) for dates.");
        } catch (NumberFormatException n) {
            modifyTaskPresenter.prepareFailView("Invalid Priority. Must be a whole number.");
        }
    }

    @Override
    public void switchToTaskListView() {
        modifyTaskPresenter.switchToTaskListView();
    }
}
