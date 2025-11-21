package entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Completable{

    private String taskName;
    private LocalDateTime deadline;
    private String taskGroup;
    private boolean status;
    private int priority; //May use different data object.


    Task(String taskName,
         LocalDateTime deadline,
         String taskGroup,
         boolean status,
         int priority) {
        this.taskName = taskName;
        this.deadline = deadline;
        this.taskGroup = taskGroup;
        this.status = status;
        this.priority = priority;
    }

    @Override
    public void complete() { this.status = true; }

    @Override
    public boolean isCompleted() { return status; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public String getTaskGroup() { return taskGroup; }
    public void setTaskGroup(String taskGroup) { this.taskGroup = taskGroup; }

    public boolean getStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public int getPriority() { return priority; }
    // Might need modification.
    public void setPriority(int priority) { this.priority = priority; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(task.taskName, taskName) && Objects.equals(task.deadline, deadline);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", deadline=" + deadline +
                ", taskGroup='" + taskGroup + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                '}';
    }








}
