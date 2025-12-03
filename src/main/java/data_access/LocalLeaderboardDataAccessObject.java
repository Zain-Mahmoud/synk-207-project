package data_access;

import entities.Habit;
import use_case.gateways.HabitGateway;
import use_case.gateways.LeaderboardGateway;

import java.util.List;
import java.util.Map;

/**
 * Adapter that wraps HabitGateway to implement LeaderboardGateway.
 * This allows leaderboard to use local CSV data when Google Sheets is not configured.
 */
public class LocalLeaderboardDataAccessObject implements LeaderboardGateway {
    private final HabitGateway habitGateway;

    public LocalLeaderboardDataAccessObject(HabitGateway habitGateway) {
        this.habitGateway = habitGateway;
    }

    @Override
    public Map<String, List<Habit>> getAllUsersWithHabits() {
        return habitGateway.getAllUsersWithHabits();
    }
}

