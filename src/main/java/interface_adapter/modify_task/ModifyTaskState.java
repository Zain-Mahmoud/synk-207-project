package interface_adapter.modify_task;

import java.time.LocalDateTime;


public class ModifyTaskState {
    private String newTaskName = "";
    private int priority = 0;
    private String deadline = "";
    private boolean status = false;


    public String getNewTaskName() {
        return newTaskName;
    }

    public void setNewTaskName(String newTaskName) {
        this.newTaskName = newTaskName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
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
}
