package interface_adapter.view_stats;

public class ViewStatsState {
    private int longestHabitStreak;
    private int numTasksCompleted;
    private int numHabitsCompleted;
    private int totalTasks;
    private int totalHabits;

    public int getLongestHabitStreak() {
        return longestHabitStreak;
    }

    public int getTotalHabits() {
        return totalHabits;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getNumHabitsCompleted() {
        return numHabitsCompleted;
    }

    public int getNumTasksCompleted() {
        return numTasksCompleted;
    }

    public void setLongestHabitStreak(int longestHabitStreak) {
        this.longestHabitStreak = longestHabitStreak;
    }

    public void setNumTasksCompleted(int numTasksCompleted) {
        this.numTasksCompleted = numTasksCompleted;
    }

    public void setNumHabitsCompleted(int numHabitsCompleted) {
        this.numHabitsCompleted = numHabitsCompleted;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public void setTotalHabits(int totalHabits) {
        this.totalHabits = totalHabits;
    }
}
