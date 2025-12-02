package use_case.view_tasks_and_habits;

import interface_adapter.logged_in.LoggedInViewModel;

/**
 * Input Boundary for actions which are related to create a task.
 */
public interface ViewTasksAndHabitsInputBoundary {

    /**
     * Fetches tasks and habits from the task and habit gateways and then formatts them into ArrayLists of strings.
     * @param loggedInViewModel the view model for the logged in use case/
     */
    void getFormattedTasksAndHabits(LoggedInViewModel loggedInViewModel);

}
