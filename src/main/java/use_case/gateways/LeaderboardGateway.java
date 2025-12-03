package use_case.gateways;

import java.util.List;
import java.util.Map;

import entities.Habit;

/**
 * Gateway interface specifically for leaderboard functionality.
 * This allows leaderboard to use a separate data source (e.g., Google Sheets)
 * while other features continue using local CSV storage.
 */
public interface LeaderboardGateway {
    /**
     * Retrieves all users with their habits for leaderboard display.
     * This method is read-only and does not modify data.
     *
     * @return Map where key is username and value is list of habits for that user
     */
    Map<String, List<Habit>> getAllUsersWithHabits();
}

