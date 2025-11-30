package strategy;

import entities.Completable;
import entities.Habit;
import entities.Task;

import java.util.ArrayList;
import java.util.Collections;

public class SortByDeadlineStrategy implements SortingStrategy {

    /**
     * Sorts the list in place by deadline in descending order
     *
     * @param completableList
     * @return
     */
    @Override
    public void sortTask(ArrayList<Task> completableList) {       // Create a mutable copy of the list

        int n = completableList.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (completableList.get(j).getDueDate().isAfter(completableList.get(j + 1).getDueDate())) {
                    Collections.swap(completableList, j, j + 1);
                }
            }
        }
    }

    /**
     * Sorts the list of habits in place by deadline in descending order
     *
     * @param completableList
     * @return
     */
    @Override
    public void sortHabit(ArrayList<Habit> completableList) {       // Create a mutable copy of the list

        int n = completableList.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (completableList.get(j).getDueDate().isAfter(completableList.get(j + 1).getDueDate())) {
                    Collections.swap(completableList, j, j + 1);
                }
            }
        }
    }
}
