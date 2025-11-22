package interface_adapter.modify_task;


public class ModifyTaskState {
    private String newTaskName = "";
    private String priority = "";
    private String deadline = "";
    private boolean status = false;
    private String taskError;


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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTaskError() {
        return taskError;
    }

    public void setTaskError(String taskError) {
        this.taskError = taskError;
    }
}
