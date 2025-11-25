package data_access;

import entities.Habit;
import use_case.gateways.HabitGateway;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of the DAO for storing Habit data.
 * This implementation does NOT persist data between runs of the program.
 * It is primarily used for testing purposes.
 */
public class InMemoryHabitDataAccessObject implements HabitGateway {

    private final Map<String, ArrayList<Habit>> userHabits = new HashMap<>();

    public InMemoryHabitDataAccessObject() {
        // Data is initialized as empty, ready to be used in memory.
    }

    @Override
    public String addHabit(String userId, Habit habit) {
        ArrayList<Habit> habitsForUser = userHabits.computeIfAbsent(userId, key -> new ArrayList<>());

        // Check for duplicates before adding
        if (habitsForUser.contains(habit)) {
            return "Error Adding Habit: Habit already exists";
        }

        habitsForUser.add(habit);
        return "Habit Added Successfully";
    }

    @Override
    public ArrayList<Habit> fetchHabits(String userId) {
        // Return a new list to prevent outside modification of the internal map object
        ArrayList<Habit> habits = userHabits.get(userId);

        if (habits == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(habits);
    }

    @Override
    public boolean deleteHabit(String userId, Habit habit) {
        ArrayList<Habit> habits = userHabits.get(userId);
        if (habits == null) {
            return false;
        }

        return habits.remove(habit);
    }



    public List<String> getAllUsernames() {
        return new ArrayList<>(userHabits.keySet());
    }


    public List<Habit> getHabitsForUser(String username) {
        ArrayList<Habit> habits = userHabits.get(username);
        if (habits == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(habits);
    }


    public Map<String, List<Habit>> getAllUsersWithHabits() {
        Map<String, List<Habit>> result = new HashMap<>();
        for (Map.Entry<String, ArrayList<Habit>> entry : userHabits.entrySet()) {
            // Ensure we return copies of the lists to maintain immutability
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }
}
