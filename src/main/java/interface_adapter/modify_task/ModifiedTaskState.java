package interface_adapter.modify_task;

import java.time.LocalDateTime;


public class ModifiedTaskState {
    private String newTaskName = "";
    private int priority = 0;
    private LocalDateTime deadline = LocalDateTime.now();
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

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
