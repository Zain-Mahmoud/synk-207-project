package use_case.view_leaderboard;

import entities.Habit;
import use_case.gateways.HabitGateway;

import java.util.*;

/**
 * The Interactor for the View Leaderboard Use Case.
 * Calculates each user's maximum streak count and sorts them for display.
 */
public class ViewLeaderboardInteractor implements ViewLeaderboardInputBoundary {
    private final HabitGateway userDataAccess;
    private final ViewLeaderboardOutputBoundary presenter;

    public ViewLeaderboardInteractor(HabitGateway userDataAccess,
                                     ViewLeaderboardOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewLeaderboardInputData inputData) {
        try {
            // Get all users with their habits
            Map<String, List<Habit>> allUsersWithHabits = userDataAccess.getAllUsersWithHabits();

            // Calculate maximum streak for each user
            List<Map<String, Object>> leaderboardEntries = new ArrayList<>();

            for (Map.Entry<String, List<Habit>> entry : allUsersWithHabits.entrySet()) {
                String username = entry.getKey();
                List<Habit> habits = entry.getValue();

                // Find the maximum streak count among all habits
                int maxStreak = 0;
                if (!habits.isEmpty()) {
                    maxStreak = habits.stream()
                            .mapToInt(Habit::getStreakCount)
                            .max()
                            .orElse(0);
                }

                // Create leaderboard entry
                Map<String, Object> entryData = new HashMap<>();
                entryData.put("username", username);
                entryData.put("maxStreak", maxStreak);
                leaderboardEntries.add(entryData);
            }

            // Sort by maxStreak in descending order
            leaderboardEntries.sort((a, b) -> {
                Integer streakA = getIntegerSafely(a.get("maxStreak"));
                Integer streakB = getIntegerSafely(b.get("maxStreak"));
                // Handle null values: nulls go to the end
                if (streakA == null && streakB == null) return 0;
                if (streakA == null) return 1;
                if (streakB == null) return -1;
                return streakB.compareTo(streakA); // Descending order
            });

            // Add rank to each entry
            for (int i = 0; i < leaderboardEntries.size(); i++) {
                leaderboardEntries.get(i).put("rank", i + 1);
            }

            ViewLeaderboardOutputData outputData = new ViewLeaderboardOutputData(leaderboardEntries);
            presenter.prepareSuccessView(outputData);

        } catch (RuntimeException e) {
            // Catch runtime exceptions (e.g., data access errors)
            presenter.prepareFailView("Failed to load leaderboard: " + e.getMessage());
        } catch (Exception e) {
            // Catch other unexpected exceptions
            presenter.prepareFailView("An unexpected error occurred while loading the leaderboard: " + e.getMessage());
        }
    }

    /**
     * Safely converts an Object to Integer, handling various number types and null values.
     *
     * @param value the value to convert
     * @return Integer value, or null if conversion is not possible
     */
    private Integer getIntegerSafely(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        // Try parsing string representation
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

