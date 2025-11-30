package strategy;

import entities.Habit;
import entities.Task;

import java.util.ArrayList;

public interface SortingStrategy {

    /**
     * Sorts the list of tasks in-place in descending order based on a defined strategy
     * @param completablesList
     * @return
     */
    void sortTask(ArrayList<Task> completablesList);

    /**
     * Sorts the list of tasks in-place in descending order based on a defined strategy
     * @param completablesList
     * @return
     */
    void sortHabit(ArrayList<Habit> completablesList);

}
