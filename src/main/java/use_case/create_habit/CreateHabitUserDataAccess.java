package use_case.create_habit;

import entities.Habit;

public interface CreateHabitUserDataAccess {
    void save(String username, Habit habit);
    boolean existsByName(String username, String habitName);
}
