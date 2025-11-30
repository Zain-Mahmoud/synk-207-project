package use_case.modify_task;

import entities.Task;
import entities.TaskBuilder;
import use_case.gateways.TaskGateway;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Locale;

public class ModifyTaskInteractor implements ModifyTaskInputBoundary {
    private final ModifyTaskOutputBoundary modifyTaskPresenter;
    private final TaskGateway taskDataAccessObject;

    public ModifyTaskInteractor(ModifyTaskOutputBoundary modifyTaskPresenter, TaskGateway taskDataAccessObject) {
        this.modifyTaskPresenter = modifyTaskPresenter;
        this.taskDataAccessObject = taskDataAccessObject;
    }

    /**
     * Attempts to parse a deadline string using multiple supported formats.
     * Supported formats:
     * 1. 'dd MMMM, yyyy HH:mm' (e.g., 01 February, 2026 06:00)
     * 2. 'YYYY-MM-DDTHH:MM[:SS]' (e.g., 2026-02-01T06:00:00 or 2026-02-01T06:00)
     * @param deadline The string representation of the deadline.
     * @return The parsed LocalDateTime object.
     * @throws java.time.format.DateTimeParseException if parsing fails for all formats.
     */
    private LocalDateTime parseFlexibleDateTime(String deadline) throws java.time.format.DateTimeParseException {
        // 1. Custom format: "01 February, 2026 06:00"
        final DateTimeFormatter CUSTOM_FORMAT = DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm", Locale.ENGLISH);

        // 2. Flexible ISO format: "2026-02-01T06:00" or "2026-02-01T06:00:00"
        final DateTimeFormatter ISO_FLEXIBLE_FORMAT = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .appendOptional(new DateTimeFormatterBuilder().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter())
                .toFormatter();

        DateTimeFormatter[] formatters = {CUSTOM_FORMAT, ISO_FLEXIBLE_FORMAT};

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(deadline, formatter);
            } catch (java.time.format.DateTimeParseException ignored) {
                // Ignore and try the next formatter
            }
        }

        // If all fail, throw the final exception
        throw new java.time.format.DateTimeParseException("Unable to parse date: " + deadline, deadline, 0);
    }

    public void execute(ModifyTaskInputData modifyInputData){

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
            // 1. Validate and Parse Input
            LocalDateTime newDeadlineFormatted = parseFlexibleDateTime(newDeadline);
            int newPriorityFormatted = Integer.parseInt(newTaskPriority);
            int oldPriorityFormatted = Integer.parseInt(oldTaskPriority);

            if (newDeadlineFormatted.isBefore(LocalDateTime.now())) {
                modifyTaskPresenter.prepareFailView("New deadline cannot be in the past.");
                return;
            }

            LocalDateTime oldDeadlineFormatted = parseFlexibleDateTime(oldDeadline);

            // 2. Build the 'Old' Task for Deletion/Comparison
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
                if (task.getName().equals(modifiedTask.getName()) && !task.equals(oldTask)){
                    modifyTaskPresenter.prepareFailView("Task already exists");
                    return;
                }
            }

            // 5. Execute Modification (Delete original, Add modified)
            taskDataAccessObject.deleteTask(userID, oldTask);
            taskDataAccessObject.addTask(userID, modifiedTask);


            modifyTaskPresenter.prepareSuccessView(new ModifyTaskOutputData(taskDataAccessObject.fetchTasks(userID)));
        } catch (java.time.format.DateTimeParseException d) {
            // Updated error message to list the supported formats
            modifyTaskPresenter.prepareFailView("Invalid date/time format. Supported formats are: 'dd MMMM, yyyy HH:mm' (e.g., 01 February, 2026 06:00) or 'YYYY-MM-DDTHH:MM[:SS]' (e.g., 2026-02-01T06:00:00).");
        } catch (NumberFormatException n) {
            modifyTaskPresenter.prepareFailView("Invalid Priority. Must be a whole number.");
        }
    }

    @Override
    public void switchToTaskListView() {
        modifyTaskPresenter.switchToTaskListView();
    }
}