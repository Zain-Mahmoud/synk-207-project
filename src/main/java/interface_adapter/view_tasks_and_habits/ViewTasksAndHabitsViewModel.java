package interface_adapter.view_tasks_and_habits;

import data_access.TaskDataAccessObject;
import data_access.HabitDataAccessObject;
import data_access.FileUserDataAccessObject;

import interface_adapter.ViewModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;

/**
 * The View Model for the Login View.
 */
public class ViewTasksAndHabitsViewModel extends ViewModel<ViewTasksAndHabitsState> {

    public static final String[] taskCols = {"Task", "Deadline", "Task Group", "Status", "Priority", "Description"};
    public static final String[] habitCols = {"Habit", "Start Date Time", "Frequency", "Last Date Time Completed", "Habit Group", "Streak Count", "Priority", "Status", "Description"};

    public ViewTasksAndHabitsViewModel() {
        super("view tasks and habits");
        setState(new ViewTasksAndHabitsState());
    }

}
