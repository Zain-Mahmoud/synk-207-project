package use_case.view_tasks_and_habits;

import entities.Habit;
import entities.Task;

import java.util.ArrayList;

public interface ViewTasksAndHabitsUserDataAccessInterface {

    ArrayList<Task>  fetchTasks();
    ArrayList<Habit> fetchabits();
}
