package use_case.modify_task;

public class ModifyTaskInputData {

    private final String userID;
    private final String oldTaskName;
    private final String oldPriority;
    private final String oldDeadline;
    private final String oldStartDateTime;
    private final Boolean oldTaskStatus;
    private final String oldTaskGroup;
    private final String oldDescription;
    private final String newTaskName;
    private final String newPriority;
    private final String newDeadline;
    private final String newStartDateTime;
    private final Boolean newTaskStatus;
    private final String newTaskGroup;
    private final String newDescription;

    public ModifyTaskInputData(String oldTaskName, String oldPriority,
                               String oldDeadline, String oldStartDateTime, Boolean oldTaskStatus, String oldTaskGroup,
                               String oldDescription, String newTaskName, String newPriority, String newDeadline,
                               String newStartDateTime, Boolean newTaskStatus,
                               String newTaskGroup, String newDescription, String userID) {
        this.oldTaskName = oldTaskName;
        this.oldPriority = oldPriority;
        this.oldDeadline = oldDeadline;
        this.oldStartDateTime = oldStartDateTime;
        this.oldTaskStatus = oldTaskStatus;
        this.oldTaskGroup = oldTaskGroup;
        this.oldDescription = oldDescription;
        this.newTaskName = newTaskName;
        this.newPriority = newPriority;
        this.newDeadline = newDeadline;
        this.newStartDateTime = newStartDateTime;
        this.newTaskStatus = newTaskStatus;
        this.newTaskGroup = newTaskGroup;
        this.newDescription = newDescription;
        this.userID = userID;
    }

    public String getOldTaskName() {
        return oldTaskName;
    }

    public String getOldPriority() {
        return oldPriority;
    }

    public String getOldDeadline() {
        return oldDeadline;
    }

    public String getOldStartDateTime() {
        return oldStartDateTime;
    }

    public Boolean getOldTaskStatus() {
        return oldTaskStatus;
    }

    public String getOldTaskGroup() {
        return oldTaskGroup;
    }

    public String getOldDescription() {
        return oldDescription;
    }

    public String getNewPriority() {
        return newPriority;
    }

    public String getNewTaskName() {
        return newTaskName;
    }

    public String getNewDeadline() {
        return newDeadline;
    }

    public String getNewStartDateTime() {
        return newStartDateTime;
    }

    public Boolean getNewTaskStatus() {
        return newTaskStatus;
    }

    public String getNewTaskGroup() {
        return newTaskGroup;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public String getUserID() {
        return this.userID;
    }
}
