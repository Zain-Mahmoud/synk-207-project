package use_case.gateways;

// CRUD GET (read), PUT (create and update), POST (create - if we don't have `id` or `uuid`), and DELETE (delete)

import entities.Habit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface HabitGateway {

    ArrayList<Habit> fetchHabits(String userId);

    boolean deleteHabit(String userId, Habit habit);

    String addHabit(String userId, Habit habit);

    Map<String, List<Habit>> getAllUsersWithHabits();

    List<String> getAllUsernames();

    List<Habit> getHabitsForUser(String username);


}