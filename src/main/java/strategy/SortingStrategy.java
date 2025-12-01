package strategy;

import entities.Completable;

import java.util.ArrayList;

public interface SortingStrategy {
    /**
     * Sorts the completablesList in-place in descending order based on a defined strategy
     * @param completablesList
     * @return
     */
    void sort(ArrayList<Completable> completablesList);

}
