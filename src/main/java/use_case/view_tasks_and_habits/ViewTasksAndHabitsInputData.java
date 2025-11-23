package use_case.view_tasks_and_habits;
import java.time.LocalDateTime;
/**
 * The Input Data for the Create Task Use Case.
 */
public class ViewTasksAndHabitsInputData {

    private final String designation;
    private final int col;
    private final String completeableName;
    private final Object changedValue;

    public ViewTasksAndHabitsInputData(String designation, int col, String completeableName, Object changedValue) {
        this.designation = designation;
        this.col = col;
        this.completeableName = completeableName;
        this.changedValue = changedValue;
    }

    String getDesignation() {return designation;}
    int getCol() {return col;}
    String getCompleteableName() {return completeableName;}
    Object getChangedValue() {return changedValue;}


}
