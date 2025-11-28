package entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Completable, Cloneable {

    private String taskName;
    private String description;
    private LocalDateTime deadline;
    private String taskGroup;
    private boolean status;
    private int priority; //May use different data object.
    private LocalDateTime startTime;

    // ADDED startTime to the constructor signature and initialization
    Task(String taskName, LocalDateTime deadline, String taskGroup, boolean status, int priority, String description, LocalDateTime startTime) {
        this.taskName = taskName;
        this.deadline = deadline;
        this.taskGroup = taskGroup;
        this.status = status;
        this.priority = priority;
        this.description = description;
        this.startTime = startTime; // ADDED
    }

    @Override
    public void complete() { this.status = true; }

    @Override
    public boolean isCompleted() { return status; }

    public String getName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

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
        // IMPROVED: Added more fields for a reliable equality check
        return status == task.status && priority == task.priority &&
                Objects.equals(taskName, task.taskName) &&
                Objects.equals(description, task.description) &&
                Objects.equals(deadline, task.deadline) &&
                Objects.equals(taskGroup, task.taskGroup) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, deadline, taskGroup, status, priority, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", startTime=" + startTime +
                ", deadline=" + deadline +
                ", taskGroup='" + taskGroup + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", description='" + description + '\'' +
                '}';
    }


    public String getDescription() {
        return description;
    }

    /**
     * @return
     */
    @Override
    public String getTitle() {
        return this.taskName;
    }

    /**
     * @return
     */
    @Override
    public Object getDueDate() {
        return this.deadline;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task clone(){
        try{
            return (Task) super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }

    }
}