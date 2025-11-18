package interface_adapter.create_task;

import java.time.LocalDateTime;

public class CreateTaskState {
    private String taskName = "";
    private LocalDateTime deadline = null;
    private String taskGroup = "";
    private int priority = 0;
    private String errorMessage = null;
    private String successMessage = null;

    public CreateTaskState(CreateTaskState copy) {
        this.taskName = copy.taskName;
        this.deadline = copy.deadline;
        this.taskGroup = copy.taskGroup;
        this.priority = copy.priority;
        this.errorMessage = copy.errorMessage;
        this.successMessage = copy.successMessage;
    }

    public CreateTaskState() {
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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
