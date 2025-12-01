package use_case.modify_task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Locale;

import entities.Task;
import entities.TaskBuilder;
import strategy.TaskValidationStrategy;
import strategy.ValidationStrategy;
import use_case.gateways.TaskGateway;

public class ModifyTaskInteractor implements ModifyTaskInputBoundary {
    private final ModifyTaskOutputBoundary modifyTaskPresenter;
    private final TaskGateway taskDataAccessObject;
    private final ValidationStrategy<Task> taskValidation;

    public ModifyTaskInteractor(ModifyTaskOutputBoundary modifyTaskPresenter, TaskGateway taskDataAccessObject) {
        this.modifyTaskPresenter = modifyTaskPresenter;
        this.taskDataAccessObject = taskDataAccessObject;
        this.taskValidation = new TaskValidationStrategy(taskDataAccessObject);
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
        final DateTimeFormatter customFormat = DateTimeFormatter.ofPattern("dd MMMM, yyyy HH:mm", Locale.ENGLISH);

        final DateTimeFormatter isoFlexibleFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .appendOptional(new DateTimeFormatterBuilder().appendLiteral(':')
                        .appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter())
                .toFormatter();

        final DateTimeFormatter[] formatters = {customFormat, isoFlexibleFormat};

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(deadline, formatter);
            } catch (java.time.format.DateTimeParseException ignoredException) {
            }
        }

        throw new java.time.format.DateTimeParseException("Unable to parse date: " + deadline, deadline, 0);
    }

    /**
     * Executes the ModifyTask use case.
     * @param modifyInputData the input data
     */
    public void execute(ModifyTaskInputData modifyInputData) {

        final String oldTaskName = modifyInputData.getOldTaskName();
        final String oldTaskPriority = modifyInputData.getOldPriority();
        final boolean oldTaskStatus = modifyInputData.getOldTaskStatus();
        final String oldDeadline = modifyInputData.getOldDeadline();
        final String oldStartDateTime = modifyInputData.getOldStartDateTime();

        final String oldTaskGroup = modifyInputData.getOldTaskGroup();
        final String oldDescription = modifyInputData.getOldDescription();

        final String newTaskName = modifyInputData.getNewTaskName();
        final String newTaskPriority = modifyInputData.getNewPriority();
        final boolean newTaskStatus = modifyInputData.getNewTaskStatus();
        final String newDeadline = modifyInputData.getNewDeadline();
        final String newStartDateTime = modifyInputData.getNewStartDateTime();
        final String newTaskGroup = modifyInputData.getNewTaskGroup();
        final String newDescription = modifyInputData.getNewDescription();
        final String userID = modifyInputData.getUserID();

        try {
            final LocalDateTime newDeadlineFormatted = parseFlexibleDateTime(newDeadline);
            final int newPriorityFormatted = Integer.parseInt(newTaskPriority);
            final int oldPriorityFormatted = Integer.parseInt(oldTaskPriority);

            if (newDeadlineFormatted.isBefore(LocalDateTime.now())) {
                modifyTaskPresenter.prepareFailView("New deadline cannot be in the past.");
                return;
            }

            final LocalDateTime oldDeadlineFormatted = parseFlexibleDateTime(oldDeadline);
            LocalDateTime oldStartFormatted = null;
            if (oldStartDateTime != null && !oldStartDateTime.isBlank()) {
                oldStartFormatted = parseFlexibleDateTime(oldStartDateTime);
            }

            final Task oldTask = new TaskBuilder()
                    .setTaskName(oldTaskName)
                    .setDescription(oldDescription)
                    .setStartTime(oldStartFormatted)
                    .setDeadline(oldDeadlineFormatted)
                    .setTaskGroup(oldTaskGroup)
                    .setPriority(oldPriorityFormatted)
                    .setStatus(oldTaskStatus).build();

            final Task modifiedTask = oldTask.clone();
            modifiedTask.setTaskName(newTaskName);
            modifiedTask.setDescription(newDescription);
            modifiedTask.setDeadline(newDeadlineFormatted);
            if (newStartDateTime != null && !newStartDateTime.isBlank()) {
                final LocalDateTime newStartFormatted = parseFlexibleDateTime(newStartDateTime);
                modifiedTask.setStartTime(newStartFormatted);
            }
            modifiedTask.setTaskGroup(newTaskGroup);
            modifiedTask.setPriority(newPriorityFormatted);
            modifiedTask.setStatus(newTaskStatus);


            String response  = taskValidation.validate(userID, oldTask, modifiedTask);
            if (response != null) {
                modifyTaskPresenter.prepareFailView(response);
                return;
            }
            // 5. Execute Modification (Delete original, Add modified)
            taskDataAccessObject.deleteTask(userID, oldTask);
            taskDataAccessObject.addTask(userID, modifiedTask);

            modifyTaskPresenter.prepareSuccessView(new ModifyTaskOutputData(taskDataAccessObject.fetchTasks(userID)));
        }
        catch (java.time.format.DateTimeParseException dateTimeParseException) {
            modifyTaskPresenter.prepareFailView("Invalid date/time format. "
                    + "Supported formats are: 'dd MMMM, yyyy HH:mm' (e.g., 01 February, 2026 06:00) "
                    + "or 'YYYY-MM-DDTHH:MM[:SS]' (e.g., 2026-02-01T06:00:00).");
        }
        catch (NumberFormatException numberFormatException) {
            modifyTaskPresenter.prepareFailView("Invalid Priority. Must be a whole number.");
        }
    }

    @Override
    public void switchToTaskListView() {
        modifyTaskPresenter.switchToTaskListView();
    }
}
