package interface_adapter.view_tasks_and_habits;

import interface_adapter.ViewModel;



/**
 * The View Model for the Login View.
 */
public class ViewTasksAndHabitsViewModel extends ViewModel<ViewTasksAndHabitsState> {

    public static final String[] taskCols = {"Task", "Start time", "Deadline", "Task Group", "Status", "Priority", "Description"};
    public static final String[] habitCols = {"Habit", "Start time", "Frequency", "Habit Group", "Streak count", "Priority", "Status", "Description"};

    public ViewTasksAndHabitsViewModel() {
        super("view tasks and habits");
        setState(new ViewTasksAndHabitsState());
    }

}
