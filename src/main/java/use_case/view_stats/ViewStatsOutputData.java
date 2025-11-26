package use_case.view_stats;

public class ViewStatsOutputData {
    private final int longestHabitStreak;
    private final int numTasksCompleted;
    private final int numHabitsCompleted;
    private final int totalTasks;
    private final int totalHabits;

    public ViewStatsOutputData(int longestHabitStreak, int numTasksCompleted, int numHabitsCompleted, int totalTasks,
                               int totalHabits) {
        this.longestHabitStreak = longestHabitStreak;
        this.numTasksCompleted = numTasksCompleted;
        this.numHabitsCompleted = numHabitsCompleted;
        this.totalTasks = totalTasks;
        this.totalHabits = totalHabits;
    }

    public int getNumHabitsCompleted() {
        return numHabitsCompleted;
    }

    public int getNumTasksCompleted() {
        return numTasksCompleted;
    }

    public int getLongestHabitStreak() {
        return longestHabitStreak;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getTotalHabits() {
        return totalHabits;
    }
}
