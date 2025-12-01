package interface_adapter.create_task;

import java.time.LocalDateTime;

public class CreateTaskState {
    private String username = "";
    private String taskName = "";
    private String description = "";
    private LocalDateTime startTime = null;
    private LocalDateTime deadline = null;
    private String taskGroup = "";
    private int priority = 0;
    private String errorMessage = null;
    private String successMessage = null;

    public CreateTaskState(CreateTaskState copy) {
        this.username = copy.username;
        this.taskName = copy.taskName;
        this.description = copy.description;
        this.startTime = copy.startTime;
        this.deadline = copy.deadline;
        this.taskGroup = copy.taskGroup;
        this.priority = copy.priority;
        this.errorMessage = copy.errorMessage;
        this.successMessage = copy.successMessage;
    }

    public CreateTaskState() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}
