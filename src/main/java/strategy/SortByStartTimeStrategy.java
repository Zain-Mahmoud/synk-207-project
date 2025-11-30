package strategy;

import entities.Completable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class SortByStartTimeStrategy implements SortingStrategy {
    /**
     * Sorts the list by start time in descending order (newest first)
     * @param completableList
     */
    @Override
    public void sort(ArrayList<Completable> completableList) {
        int n = completableList.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                LocalDateTime a = completableList.get(j).getStartTime();
                LocalDateTime b = completableList.get(j + 1).getStartTime();
                if (a == null && b != null) {
                    Collections.swap(completableList, j, j + 1);
                } else if (a != null && b != null && a.isBefore(b)) {
                    Collections.swap(completableList, j, j + 1);
                }
            }
        }
    }
}
