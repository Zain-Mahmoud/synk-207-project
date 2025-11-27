package interface_adapter.view_tasks_and_habits;

import entities.Habit;
import entities.Task;

import java.util.ArrayList;

/**
 * The state for the View Tasks And Habits View Model.
 */
public class ViewTasksAndHabitsState {
    
    private  ArrayList<ArrayList<String>> formattedTasks = new ArrayList<>();
    private ArrayList<ArrayList<String>> formattedHabits = new ArrayList<>();
    
    public ViewTasksAndHabitsState() {}
    
    public ViewTasksAndHabitsState(ArrayList<ArrayList<String>> formattedTasks, ArrayList<ArrayList<String>> formattedHabits){
        this.formattedTasks = formattedTasks;
        this.formattedHabits = formattedHabits;
    }
    
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




