package use_case.modify_habit;

public class ModifyHabitInputData {
    private final String userID;

    private final String oldHabitName;
    private final String oldPriority;
    private final Boolean oldHabitStatus;
    private final String oldStartDateTime;
    private final String oldStreakCount;
    private final String oldHabitGroup;
    private final String oldFrequency;

    private final String newHabitName;
    private final String newPriority;
    private final Boolean newHabitStatus;
    private final String newStartDateTime;
    private final String newStreakCount;
    private final String newHabitGroup;
    private final String newFrequency;

    public ModifyHabitInputData(String oldHabitName, String oldPriority, Boolean oldHabitStatus, 
                                String oldStartDateTime, String oldStreakCount, String oldHabitGroup,
                                String oldFrequency, String newHabitName, String newPriority, Boolean newHabitStatus,
                                String newStartDateTime, String newStreakCount, String newHabitGroup,
                                String newFrequency, String userID) {

        this.oldHabitName = oldHabitName;
        this.oldPriority = oldPriority;
        this.oldHabitStatus = oldHabitStatus;
        this.oldStartDateTime = oldStartDateTime;
        this.oldStreakCount = oldStreakCount;
        this.oldHabitGroup = oldHabitGroup;
        this.oldFrequency = oldFrequency;

        this.newHabitName = newHabitName;
        this.newPriority = newPriority;
        this.newHabitStatus = newHabitStatus;
        this.newStartDateTime = newStartDateTime;
        this.newStreakCount = newStreakCount;
        this.newHabitGroup = newHabitGroup;
        this.newFrequency = newFrequency;
        
        this.userID = userID;
    }

    public String getOldHabitName() {
        return oldHabitName;
    }

    public String getOldPriority() {
        return oldPriority;
    }

    public Boolean getOldHabitStatus() {
        return oldHabitStatus;
    }

    public String getOldStartDateTime() {
        return oldStartDateTime;
    }

    public String getOldStreakCount() {
        return oldStreakCount;
    }

    public String getOldHabitGroup() {
        return oldHabitGroup;
    }

    public String getOldFrequency() {
        return oldFrequency;
    }

    public String getNewPriority() {
        return newPriority;
    }

    public String getNewHabitName() {
        return newHabitName;
    }

    public Boolean getNewHabitStatus() {
        return newHabitStatus;
    }

    public String getNewStartDateTime() {
        return newStartDateTime;
    }

    public String getNewStreakCount() {
        return newStreakCount;
    }

    public String getNewHabitGroup() {
        return newHabitGroup;
    }

    public String getNewFrequency() {
        return newFrequency;
    }

    public String getUserID() {
        return this.userID;
    }
}
