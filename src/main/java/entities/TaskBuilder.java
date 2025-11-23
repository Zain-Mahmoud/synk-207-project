package entities;

import java.time.LocalDateTime;

public class TaskBuilder {
    private String taskName;
    private String description;
    private LocalDateTime deadline;
    private String taskGroup;
    private boolean status;
    private int priority; //May use different data object.
    private LocalDateTime startTime;

    public TaskBuilder setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public TaskBuilder setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        return this;
    }

    public TaskBuilder setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
        return this;
    }

    public TaskBuilder setStatus(boolean status) {
        this.status = status;
        return this;
    }

    public TaskBuilder setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public TaskBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public Task build() {
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalStateException("taskName must not be null or empty");
        }
        return new Task(taskName, deadline, taskGroup, status, priority, description);
    }


    public TaskBuilder setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

}
