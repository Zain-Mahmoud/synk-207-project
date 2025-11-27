package use_case.modify_task;

public class ModifyTaskInputData {
    private final String userID;
    private final String oldTaskName;
    private final String oldPriority;
    private final String oldDeadline;
    private final Boolean oldTaskStatus;
    private final String oldTaskGroup;
    private final String oldDescription;
    private final String oldStartTime; // ADDED
    private final String newTaskName;
    private final String newPriority;
    private final String newDeadline;
    private final Boolean newTaskStatus;
    private final String newTaskGroup;
    private final String newDescription;
    private final String newStartTime; // ADDED

    public ModifyTaskInputData(String oldTaskName, String oldPriority, String oldDeadline, Boolean oldTaskStatus,
                               String oldTaskGroup, String oldDescription, String oldStartTime, // ADDED
                               String newTaskName, String newPriority, String newDeadline, Boolean newTaskStatus,
                               String newTaskGroup, String newDescription, String newStartTime, // ADDED
                               String userID) {
        this.oldTaskName = oldTaskName;
        this.oldPriority = oldPriority;
        this.oldDeadline = oldDeadline;
        this.oldTaskStatus = oldTaskStatus;
        this.oldTaskGroup = oldTaskGroup;
        this.oldDescription = oldDescription;
        this.oldStartTime = oldStartTime; // ADDED
        this.newTaskName = newTaskName;
        this.newPriority = newPriority;
        this.newDeadline = newDeadline;
        this.newTaskStatus = newTaskStatus;
        this.newTaskGroup = newTaskGroup;
        this.newDescription = newDescription;
        this.newStartTime = newStartTime; // ADDED
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

    public Boolean getOldTaskStatus() {
        return oldTaskStatus;
    }

    public String getOldTaskGroup() {
        return oldTaskGroup;
    }

    public String getOldDescription() {
        return oldDescription;
    }

    public String getOldStartTime() { // ADDED
        return oldStartTime;
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

    public Boolean getNewTaskStatus() {
        return newTaskStatus;
    }

    public String getNewTaskGroup() {
        return newTaskGroup;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public String getNewStartTime() { // ADDED
        return newStartTime;
    }

    public String getUserID() {
        return this.userID;
    }
}