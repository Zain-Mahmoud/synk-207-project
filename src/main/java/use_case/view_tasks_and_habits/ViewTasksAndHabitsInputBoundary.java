package use_case.view_tasks_and_habits;

/**
 * Input Boundary for actions which are related to create a task.
 */
public interface ViewTasksAndHabitsInputBoundary {

    /**
     * Fetches tasks and habits from the task and habit gateways and then formatts them into ArrayLists of strings.
     * @param inputData the input data for the view tasks and habits use case/
     */
    void getFormattedTasksAndHabits(ViewTasksAndHabitsInputData inputData);

}
