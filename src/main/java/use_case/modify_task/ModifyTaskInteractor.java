package use_case.modify_task;

import entities.Task;
import entities.TaskBuilder;
import use_case.gateways.TaskGateway;

import java.time.LocalDateTime;

public class ModifyTaskInteractor implements ModifyTaskInputBoundary {
    private final ModifyTaskOutputBoundary modifyPresenter;
    private final TaskGateway userDataAccessObject;

    public ModifyTaskInteractor(ModifyTaskOutputBoundary modifyPresenter, TaskGateway userDataAccessObject) {
        this.modifyPresenter = modifyPresenter;
        this.userDataAccessObject = userDataAccessObject;
    }

    public void execute(ModifyTaskInputData modifyInputData) {

        // Old Task Data
        String oldTaskName = modifyInputData.getOldTaskName();
        int oldTaskPriority = modifyInputData.getOldPriority();
        boolean oldTaskStatus = modifyInputData.getOldTaskStatus();
        String oldDeadline = modifyInputData.getOldDeadline();
        String oldTaskGroup = modifyInputData.getOldTaskGroup();
        String oldDescription = modifyInputData.getOldDescription();
        String oldStartTimeRaw = modifyInputData.getOldStartTime(); // ADDED

        // New Task Data
        String newTaskName = modifyInputData.getNewTaskName();
        String newTaskPriority = modifyInputData.getNewPriority();
        boolean newTaskStatus = modifyInputData.getNewTaskStatus();
        String newDeadline = modifyInputData.getNewDeadline();
        String newTaskGroup = modifyInputData.getNewTaskGroup();
        String newDescription = modifyInputData.getNewDescription();
        String newStartTimeRaw = modifyInputData.getNewStartTime(); // ADDED
        String userID = modifyInputData.getUserID();

        try{

            LocalDateTime newDeadlineFormatted = LocalDateTime.parse(newDeadline);
            LocalDateTime newStartTimeFormatted = newStartTimeRaw.isBlank() ? null : LocalDateTime.parse(newStartTimeRaw); // ADDED parsing
            int newPriorityFormatted = Integer.parseInt(newTaskPriority);

            if (newDeadlineFormatted.isBefore(LocalDateTime.now())) {
                modifyPresenter.prepareFailView("New Deadline cannot be in the past.");
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
            LocalDateTime oldStartTimeFormatted = oldStartTimeRaw.isBlank() ? null : LocalDateTime.parse(oldStartTimeRaw); // ADDED parsing

            final Task oldTask = new TaskBuilder()
                    .setTaskName(oldTaskName)
                    .setDescription(oldDescription)
                    .setDeadline(oldDeadlineFormatted)
                    .setStartTime(oldStartTimeFormatted) // ADDED setStartTime
                    .setTaskGroup(oldTaskGroup)
                    .setPriority(oldTaskPriority)
                    .setStatus(oldTaskStatus).build();

            userDataAccessObject.deleteTask(userID, oldTask);
            userDataAccessObject.addTask(userID, modifiedTask);


            modifyPresenter.prepareSuccessView();
        } catch (java.time.format.DateTimeParseException d) {
            modifyPresenter.prepareFailView("Invalid date/time format. Use ISO format (e.g., YYYY-MM-DDTHH:MM:SS) for dates.");
        } catch (NumberFormatException n) {
            modifyPresenter.prepareFailView("Invalid Priority. Must be a whole number.");
        }
    }

    @Override
    public void switchToTaskListView() {
        modifyPresenter.switchToTaskListView();
    }
}