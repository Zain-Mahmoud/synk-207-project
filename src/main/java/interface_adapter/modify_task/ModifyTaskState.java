package interface_adapter.modify_task;

public class ModifyTaskState {
    private String newTaskName = "";
    private String priority = "";
    private String deadline = "";
    private String startDateTime = "";
    private boolean status;
    private String taskGroup = "";
    private String description = "";
    private String taskError;
    private String oldTaskName = "";
    private String oldPriority = "";
    private String oldDeadline = "";
    private String oldStartDateTime = "";
    private boolean oldStatus;
    private String oldTaskGroup = "";
    private String oldDescription = "";

    public String getNewTaskName() {
        return newTaskName;
    }

    public void setNewTaskName(String newTaskName) {
        this.newTaskName = newTaskName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskError() {
        return taskError;
    }

    public void setTaskError(String taskError) {
        this.taskError = taskError;
    }

    public String getOldTaskName() {
        return oldTaskName;
    }

    public void setOldTaskName(String oldTaskName) {
        this.oldTaskName = oldTaskName;
    }

    public String getOldPriority() {
        return oldPriority;
    }

    public void setOldPriority(String oldPriority) {
        this.oldPriority = oldPriority;
    }

    public String getOldDeadline() {
        return oldDeadline;
    }

    public void setOldDeadline(String oldDeadline) {
        this.oldDeadline = oldDeadline;
    }

    public String getOldStartDateTime() {
        return oldStartDateTime;
    }

    public void setOldStartDateTime(String oldStartDateTime) {
        this.oldStartDateTime = oldStartDateTime;
    }

    public boolean getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(boolean oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getOldTaskGroup() {
        return oldTaskGroup;
    }

    public void setOldTaskGroup(String oldTaskGroup) {
        this.oldTaskGroup = oldTaskGroup;
    }

    public String getOldDescription() {
        return oldDescription;
    }

    public void setOldDescription(String oldDescription) {
        this.oldDescription = oldDescription;
    }

}
