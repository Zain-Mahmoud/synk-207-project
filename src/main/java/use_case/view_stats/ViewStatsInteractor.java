package use_case.view_stats;

import java.util.ArrayList;

import entities.Habit;
import entities.Task;
import use_case.gateways.HabitGateway;
import use_case.gateways.TaskGateway;

public class ViewStatsInteractor implements ViewStatsInputBoundary {

    private final HabitGateway habitGateway;
    private final TaskGateway taskGateway;
    private final ViewStatsOutputBoundary viewStatsPresenter;

    public ViewStatsInteractor(HabitGateway habitGateway, TaskGateway taskGateway,
                               ViewStatsOutputBoundary viewStatsPresenter) {
        this.habitGateway = habitGateway;
        this.taskGateway = taskGateway;
        this.viewStatsPresenter = viewStatsPresenter;
    }

    @Override
    public void execute(ViewStatsInputData inputData) {
        final ArrayList<Task> taskList = taskGateway.fetchTasks(inputData.getUsername());
        final ArrayList<Habit> habitList = habitGateway.fetchHabits(inputData.getUsername());

        int longestHabitStreak = 0;
        int numHabitsCompleted = 0;
        for (Habit habit : habitList) {
            if (habit.getStreakCount() > longestHabitStreak) {
                longestHabitStreak = habit.getStreakCount();
            }
            if (habit.getStatus()) {
                numHabitsCompleted += 1;
            }
        }
        int numTasksCompleted = 0;
        for (Task task : taskList) {
            if (task.getStatus()) {
                numTasksCompleted += 1;
            }
        }

        final int totalTasks = taskList.size();
        final int totalHabits = habitList.size();

        final ViewStatsOutputData viewStatsOutputData = new ViewStatsOutputData(longestHabitStreak, numTasksCompleted,
                numHabitsCompleted, totalTasks, totalHabits);

        viewStatsPresenter.prepareSuccessView(viewStatsOutputData);

    }

    @Override
    public void switchToTaskView() {
        viewStatsPresenter.switchToTaskList();
    }
}
