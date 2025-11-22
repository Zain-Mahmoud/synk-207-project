package use_case.modify_task;



public class ModifyTaskInputData {
    private final String userID;
    private final String oldTaskName;
    private final int oldPriority;
    private final String oldDeadline;
    private final Boolean oldTaskStatus;
    private final String newTaskName;
    private final String newPriority;
    private final String newDeadline;
    private final Boolean newTaskStatus;

    public ModifyTaskInputData(String oldTaskName, int oldPriority, String oldDeadline, Boolean oldTaskStatus,
                                String newTaskName, String newPriority, String newDeadline, Boolean newTaskStatus,
                                String userID) {
        this.oldTaskName = oldTaskName;
        this.oldPriority = oldPriority;
        this.oldDeadline = oldDeadline;
        this.oldTaskStatus = oldTaskStatus;
        this.newTaskName = newTaskName;
        this.newPriority = newPriority;
        this.newDeadline = newDeadline;
        this.newTaskStatus = newTaskStatus;
        this.userID = userID;
    }

    public String getOldTaskName() {
        return oldTaskName;
    }

    public int getOldPriority() {
        return oldPriority;
    }

    public String getOldDeadline() {
        return oldDeadline;
    }

    public Boolean getOldTaskStatus() {
        return oldTaskStatus;
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

    public String getUserID() {
        return this.userID;
    }
}
