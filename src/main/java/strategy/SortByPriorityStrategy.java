package strategy;

import entities.Completable;

import java.util.ArrayList;
import java.util.Collections;

public class SortByPriorityStrategy implements SortingStrategy{
    /**
     * Sorts the list by priority in place in descending order
     *
     * @param completableList
     * @return
     */
    @Override
    public void sort(ArrayList<Completable> completableList) {       // Create a mutable copy of the list

        int n = completableList.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (completableList.get(j).getPriority() < completableList.get(j+1).getPriority()) {
                    Collections.swap(completableList, j, j + 1);
                }
            }
        }
    }
}

