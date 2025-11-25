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
                Integer streakA = (Integer) a.get("maxStreak");
                Integer streakB = (Integer) b.get("maxStreak");
                return streakB.compareTo(streakA); // Descending order
            });

            // Add rank to each entry
            for (int i = 0; i < leaderboardEntries.size(); i++) {
                leaderboardEntries.get(i).put("rank", i + 1);
            }

            ViewLeaderboardOutputData outputData = new ViewLeaderboardOutputData(leaderboardEntries);
            presenter.prepareSuccessView(outputData);

        } catch (Exception e) {
            presenter.prepareFailView("Failed to load leaderboard: " + e.getMessage());
        }
    }
}

