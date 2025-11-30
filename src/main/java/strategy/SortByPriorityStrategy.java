package strategy;

import entities.Habit;
import entities.Task;

import java.util.ArrayList;
import java.util.Collections;

public class SortByPriorityStrategy implements SortingStrategy{
    /**
     * Sorts the list of tasks by priority in place in descending order
     *
     * @param taskList
     * @return
     */
    @Override
    public void sortTask(ArrayList<Task> taskList) {       // Create a mutable copy of the list

        int n = taskList.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (taskList.get(j).getPriority() < taskList.get(j+1).getPriority()) {
                    Collections.swap(taskList, j, j + 1);
                }
            }
        }
    }

    /**
     * Sorts the list of habits by priority in place in descending order
     *
     * @param taskList
     * @return
     */
    @Override
    public void sortHabit(ArrayList<Habit> taskList) {       // Create a mutable copy of the list

        int n = taskList.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (taskList.get(j).getPriority() < taskList.get(j+1).getPriority()) {
                    Collections.swap(taskList, j, j + 1);
                }
            }
        }
    }
}

