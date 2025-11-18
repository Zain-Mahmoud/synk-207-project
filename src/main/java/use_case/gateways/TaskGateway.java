import entities.Habit;
package use_case.gateways;

public interface TaskGateway{

    public TaskList getTasks();
    // get a list of all the tasks saved for this user

    public HabitList getHabits();
    // get a list of all the habits saved for this user

    public void updateTasks(TaskList tasklist);
    // push the changes made to the tasks back to the database

    public void updateHabits(HabitList habitlist);
    // push the changes made to the habits back to the database
}