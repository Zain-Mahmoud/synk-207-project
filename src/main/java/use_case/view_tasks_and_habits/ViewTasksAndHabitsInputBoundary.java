package use_case.view_tasks_and_habits;

import interface_adapter.logged_in.LoggedInViewModel;

/**
 * Input Boundary for actions which are related to create a task.
 */
public interface ViewTasksAndHabitsInputBoundary {

    /**
     * Fetches tasks and habist using the data access objects and formats them into ArrayLists of Strings,
     * sending them to the presenter.
     * @param loggedInViewModel the Logged In View Modek
     */
    void getFormattedTasksAndHabits(LoggedInViewModel loggedInViewModel);

}
