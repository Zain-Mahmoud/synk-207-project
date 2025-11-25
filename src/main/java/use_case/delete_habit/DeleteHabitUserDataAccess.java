package use_case.delete_habit;

public interface DeleteHabitUserDataAccess {
    void  deleteHabit(String username, String habit);
    boolean existsByName(String username, String habitName);
}
