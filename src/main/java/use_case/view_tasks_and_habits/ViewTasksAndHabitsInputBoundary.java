package use_case.view_tasks_and_habits;

import java.util.ArrayList;

/**
 * Input Boundary for actions which are related to create a task.
 */
public interface ViewTasksAndHabitsInputBoundary {
    /**
     * Executes the view_tasks_and_habits use case.
     */
    void execute(ViewTasksAndHabitsInputData createInputData);

    ArrayList<ArrayList<String>> getFormattedTasks();

    ArrayList<ArrayList<String>> getFormattedHabits();
}
