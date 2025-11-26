package interface_adapter.view_stats;

import interface_adapter.ViewManagerModel;
import use_case.view_stats.ViewStatsOutputBoundary;
import use_case.view_stats.ViewStatsOutputData;

public class ViewStatsPresenter implements ViewStatsOutputBoundary {
    private final ViewStatsViewModel viewStatsViewModel;
    private final ViewManagerModel viewManagerModel;

    public ViewStatsPresenter(ViewStatsViewModel viewStatsViewModel, ViewManagerModel viewManagerModel) {
        this.viewStatsViewModel = viewStatsViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Prepares success view.
     * @param outputData output data to present
     */
    public void prepareSuccessView(ViewStatsOutputData outputData) {
        final int longestHabitStreak = outputData.getLongestHabitStreak();
        final int numTasksCompleted = outputData.getNumTasksCompleted();
        final int numHabitsCompleted = outputData.getNumHabitsCompleted();
        final int totalTasks = outputData.getTotalTasks();
        final int totalHabits = outputData.getTotalHabits();

        final ViewStatsState viewStatsState = viewStatsViewModel.getState();
        viewStatsState.setLongestHabitStreak(longestHabitStreak);
        viewStatsState.setNumHabitsCompleted(numHabitsCompleted);
        viewStatsState.setNumTasksCompleted(numTasksCompleted);
        viewStatsState.setTotalHabits(totalHabits);
        viewStatsState.setTotalTasks(totalTasks);

        viewStatsViewModel.setState(viewStatsState);
        viewStatsViewModel.firePropertyChanged();
    }

    /**
     * Switches to task list.
     */
    public void switchToTaskList() {
        viewManagerModel.setState("logged in");
    }
}
