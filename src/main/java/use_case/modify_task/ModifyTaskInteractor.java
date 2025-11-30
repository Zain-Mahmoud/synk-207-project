package use_case.modify_task;

import entities.Task;
import entities.TaskBuilder;
import use_case.gateways.TaskGateway;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ModifyTaskInteractor implements ModifyTaskInputBoundary {
    private final ModifyTaskOutputBoundary modifyTaskPresenter;
    private final TaskGateway taskDataAccessObject;

    public ModifyTaskInteractor(ModifyTaskOutputBoundary modifyTaskPresenter, TaskGateway taskDataAccessObject) {
        this.modifyTaskPresenter = modifyTaskPresenter;
        this.taskDataAccessObject = taskDataAccessObject;
    }

    public void execute(ModifyTaskInputData modifyInputData){
        final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm", Locale.ENGLISH);


        final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        String oldTaskName = modifyInputData.getOldTaskName();
        String oldTaskPriority = modifyInputData.getOldPriority();
        boolean oldTaskStatus = modifyInputData.getOldTaskStatus();
        String oldDeadline = modifyInputData.getOldDeadline();
        String oldTaskGroup = modifyInputData.getOldTaskGroup();
        String oldDescription = modifyInputData.getOldDescription();

        String newTaskName = modifyInputData.getNewTaskName();
        String newTaskPriority = modifyInputData.getNewPriority();
        boolean newTaskStatus = modifyInputData.getNewTaskStatus();
        String newDeadline = modifyInputData.getNewDeadline();
        String newTaskGroup = modifyInputData.getNewTaskGroup();
        String newDescription = modifyInputData.getNewDescription();
        String userID = modifyInputData.getUserID();

        try{
            LocalDateTime newDeadlineFormatted = LocalDateTime.parse(newDeadline, INPUT_FORMAT);

            int newPriorityFormatted = Integer.parseInt(newTaskPriority);
            int oldPriorityFormatted = Integer.parseInt(oldTaskPriority);

            if (newDeadlineFormatted.isBefore(LocalDateTime.now())) {
                modifyTaskPresenter.prepareFailView("New deadline cannot be in the past.");
                return;
            }

            LocalDateTime oldDeadlineFormatted = LocalDateTime.parse(oldDeadline, INPUT_FORMAT);

            final Task oldTask = new TaskBuilder()
                    .setTaskName(oldTaskName)
                    .setDescription(oldDescription)
                    .setDeadline(oldDeadlineFormatted)
                    .setTaskGroup(oldTaskGroup)
                    .setPriority(oldPriorityFormatted)
                    .setStatus(oldTaskStatus).build();

            final Task modifiedTask = oldTask.clone();
            modifiedTask.setTaskName(newTaskName);
            modifiedTask.setDescription(newDescription);
            modifiedTask.setDeadline(newDeadlineFormatted);
            modifiedTask.setTaskGroup(newTaskGroup);
            modifiedTask.setPriority(newPriorityFormatted);
            modifiedTask.setStatus(newTaskStatus);

            ArrayList<Task> taskList = taskDataAccessObject.fetchTasks(userID);

            for (Task task : taskList) {
                if (!task.equals(oldTask) && task.getName().equals(modifiedTask.getName())){
                    modifyTaskPresenter.prepareFailView("Task already exists");
                    return;
                }
            }

            taskDataAccessObject.deleteTask(userID, oldTask);
            taskDataAccessObject.addTask(userID, modifiedTask);


            modifyTaskPresenter.prepareSuccessView(new ModifyTaskOutputData(taskDataAccessObject.fetchTasks(userID)));
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
