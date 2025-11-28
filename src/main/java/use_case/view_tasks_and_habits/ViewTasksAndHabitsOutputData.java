package use_case.view_tasks_and_habits;

import entities.Habit;

import java.util.ArrayList;


public class ViewTasksAndHabitsOutputData {

    private ArrayList<ArrayList<String>> formattedTasks =  new ArrayList<>();
    private ArrayList<ArrayList<String>> formattedHabits =  new ArrayList<>();

    public ViewTasksAndHabitsOutputData(ArrayList<ArrayList<String>> formattedTasks, ArrayList<ArrayList<String>> formattedHabits) {
        this.formattedTasks = formattedTasks;
        this.formattedHabits = formattedHabits;
    }

    public ViewTasksAndHabitsOutputData() {}

    public ArrayList<ArrayList<String>> getFormattedTasks() {
        return this.formattedTasks;
    }

    public void setFormattedTasks(ArrayList<ArrayList<String>> formattedTasks) {
        this.formattedTasks = formattedTasks;
    }

    public ArrayList<ArrayList<String>> getFormattedHabits() {
        return this.formattedHabits;
    }

    public void setFormattedHabits(ArrayList<ArrayList<String>> formattedHabits) {
        this.formattedHabits = formattedHabits;
    }
}
