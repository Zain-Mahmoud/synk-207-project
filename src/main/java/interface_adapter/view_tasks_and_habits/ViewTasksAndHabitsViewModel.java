package interface_adapter.view_tasks_and_habits;

import interface_adapter.ViewModel;

/**
 * The View Model for the Login View.
 */
public class ViewTasksAndHabitsViewModel extends ViewModel<ViewTasksAndHabitsState> {

    public static final String[] TASKCOLS = {"Task", "Start time", "Deadline", "Task Group", "Status", "Priority",
        "Description"};
    public static final String[] HABITCOLS = {"Habit", "Start time", "Frequency", "Habit Group", "Streak count",
        "Priority", "Status", "Description"};

    public static final int WINDOWWIDTH = 1200;
    public static final int WINDOWHEIGHT = 500;

    public static final int TASKNAMECOLNUM = 0;
    public static final int TASKSTARTTIMECOLNUM = 1;
    public static final int TASKDUETIMECOLNUM = 2;
    public static final int TASKGROUPCOLNUM = 3;
    public static final int TASKSTATUSCOLNUM = 4;
    public static final int TASKPRIORITYCOLNUM = 5;
    public static final int TASKDESCRIPTIONCOLNUM = 6;

    public static final int HABITNAMECOLNUM = 0;
    public static final int HABITSTARTTIMECOLNUM = 1;
    public static final int HABITFREQCOLNUM = 2;
    public static final int HABITGROUPCOLNUM = 3;
    public static final int HABITSTREAKCOUNTCOLNUM = 4;
    public static final int HABITPRIORITYCOLNUM = 5;
    public static final int HABITDESCRIPTIONCOLNUM = 6;

    public static final int BUTTONPANELROWNUM = 6;
    public static final int BUTTONPANELCOLNUM = 1;
    public static final int BUTTONPANELHGAPNUM = 5;
    public static final int BUTTONPANELVGAPNUM = 5;

    public ViewTasksAndHabitsViewModel() {
        super("view tasks and habits");
        setState(new ViewTasksAndHabitsState());
    }

}
