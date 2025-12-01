package interface_adapter.view_tasks_and_habits;

import interface_adapter.ViewModel;

/**
 * The View Model for the Login View.
 */
public class ViewTasksAndHabitsViewModel extends ViewModel<ViewTasksAndHabitsState> {

    public static final String MAINLABELTITLE = "Tasks and Habits";

    public static final String REFRESHBUTTONLABEL = "Refresh";
    public static final String EXITBUTTONLABEL = "Exit";

    public static final String[] TASKCOLS = {"Task", "Deadline", "Task Group", "Status", "Priority", "Description"};
    public static final String[] HABITCOLS = {"Habit", "Start Date Time", "Frequency",
        "Last Date Time Completed", "Habit Group", "Streak Count", "Priority", "Status", "Description"};

    public static final String TASKTABTITLE = "Tasks";
    public static final String HABITTABTITLE = "Habits";

    public static final int VIEWWIDTH = 1200;
    public static final int VIEWHEIGHT = 500;

    public ViewTasksAndHabitsViewModel() {
        super("view tasks and habits");
        setState(new ViewTasksAndHabitsState());
    }

}
