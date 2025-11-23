package use_case.view_leaderboard;

import entities.Habit;

import java.util.List;
import java.util.Map;

/**
 * Data Access Interface for the View Leaderboard Use Case.
 * Provides methods to retrieve all users and their habits for leaderboard display.
 */
public interface ViewLeaderboardUserDataAccessInterface {
    /**
     * Gets all usernames in the system.
     * @return List of all usernames
     */
    List<String> getAllUsernames();

    /**
     * Gets all habits for a specific user.
     * @param username the username
     * @return List of habits for the user
     */
    List<Habit> getHabitsForUser(String username);

    /**
     * Gets all users with their habits in a single call.
     * This is more efficient than calling getHabitsForUser for each user.
     * @return Map where key is username and value is list of habits
     */
    Map<String, List<Habit>> getAllUsersWithHabits();
}

