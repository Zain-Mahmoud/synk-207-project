package use_case.create_task;

import java.time.LocalDateTime;

/**
 * The Input Data for the CreateTask Use Case.
 */
public class CreateTaskInputData {

    private final String username;
    private final String taskName;
    private final String description;
    private final LocalDateTime startTime;
    private final LocalDateTime deadline;
    private final String taskGroup;
    private final boolean status;
    private final int priority;

    public CreateTaskInputData(String username, String taskName, String description, LocalDateTime startTime,
            LocalDateTime deadline, String taskGroup, boolean status, int priority) {
        this.username = username;
        this.taskName = taskName;
        this.description = description;
        this.startTime = startTime;
        this.deadline = deadline;
        this.taskGroup = taskGroup;
        this.status = status;
        this.priority = priority;

    }

    String getUsername() {
        return username;
    }

    String getTaskName() {
        return taskName;
    }

    LocalDateTime getStartTime() {
        return startTime;
    }

    LocalDateTime getDeadline() {
        return deadline;
    }

    String getTaskGroup() {
        return taskGroup;
    }

    boolean getstatus() {
        return status;
    }

    int getPriority() {
        return priority;
    }

    String getDescription() {
        return description;
    }

}