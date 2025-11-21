package use_case.modify_task;

import java.time.LocalDateTime;

public class ModifyTaskInputData {
    private final String userID;
    private final String newTaskName;
    private final int newPriority;
    private final LocalDateTime newDeadline;
    private final Boolean newTaskStatus;

    public ModifyTaskInputData(String newTaskName, int newPriority, LocalDateTime newDeadline, Boolean newTaskStatus, String userID) {
        this.newTaskName = newTaskName;
        this.newPriority = newPriority;
        this.newDeadline = newDeadline;
        this.newTaskStatus = newTaskStatus;
        this.userID = userID;
    }

    public int getNewPriority() {
        return newPriority;
    }

    public String getNewTaskName() {
        return newTaskName;
    }

    public LocalDateTime getNewDeadline() {
        return newDeadline;
    }

    public Boolean getNewTaskStatus() {
        return newTaskStatus;
    }

    public String getUserID() {
        return this.userID;
    }
}
