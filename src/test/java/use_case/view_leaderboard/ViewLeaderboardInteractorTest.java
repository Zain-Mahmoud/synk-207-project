package use_case.view_leaderboard;

import data_access.InMemoryHabitDataAccessObject;
import entities.Habit;
import entities.HabitBuilder;
import org.junit.jupiter.api.Test;
import use_case.gateways.HabitGateway;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ViewLeaderboardInteractor.
 * Tests the leaderboard calculation and ranking logic.
 */
class ViewLeaderboardInteractorTest {

    /**
     * Test presenter that captures output for verification.
     */
    static class TestPresenter implements ViewLeaderboardOutputBoundary {
        ViewLeaderboardOutputData successData;
        String failMessage;

        @Override
        public void prepareSuccessView(ViewLeaderboardOutputData outputData) {
            this.successData = outputData;
            this.failMessage = null;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failMessage = errorMessage;
            this.successData = null;
        }
    }

    @Test
    void execute_successfullyCalculatesLeaderboardWithMultipleUsers() {
        // Arrange
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        ViewLeaderboardInteractor interactor = new ViewLeaderboardInteractor(habitGateway, presenter);

        // User alice with max streak 15
        Habit habit1 = new HabitBuilder()
                .setHabitName("Exercise")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Health")
                .setStreakCount(15)
                .setPriority(5)
                .setStatus(true)
                .build();

        Habit habit2 = new HabitBuilder()
                .setHabitName("Reading")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 20, 0))
                .setFrequency(1)
                .setHabitGroup("Learning")
                .setStreakCount(8)
                .setPriority(3)
                .setStatus(true)
                .build();

        // User bob with max streak 30
        Habit habit3 = new HabitBuilder()
                .setHabitName("Meditation")
                .setStartDateTime(LocalDateTime.of(2023, 12, 1, 7, 0))
                .setFrequency(1)
                .setHabitGroup("Health")
                .setStreakCount(30)
                .setPriority(4)
                .setStatus(true)
                .build();

        habitGateway.addHabit("alice", habit1);
        habitGateway.addHabit("alice", habit2);
        habitGateway.addHabit("bob", habit3);

        // Act
        interactor.execute(new ViewLeaderboardInputData());

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);

        List<Map<String, Object>> entries = presenter.successData.getLeaderboardEntries();
        assertEquals(2, entries.size());

        // Verify ranking: bob should be first (streak 30), alice second (streak 15)
        Map<String, Object> firstEntry = entries.get(0);
        assertEquals("bob", firstEntry.get("username"));
        assertEquals(30, firstEntry.get("maxStreak"));
        assertEquals(1, firstEntry.get("rank"));

        Map<String, Object> secondEntry = entries.get(1);
        assertEquals("alice", secondEntry.get("username"));
        assertEquals(15, secondEntry.get("maxStreak"));
        assertEquals(2, secondEntry.get("rank"));
    }

    @Test
    void execute_handlesEmptyDataGracefully() {
        // Arrange
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        ViewLeaderboardInteractor interactor = new ViewLeaderboardInteractor(habitGateway, presenter);

        // Act
        interactor.execute(new ViewLeaderboardInputData());

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);
        assertTrue(presenter.successData.getLeaderboardEntries().isEmpty());
    }

    @Test
    void execute_handlesSingleUser() {
        // Arrange
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        ViewLeaderboardInteractor interactor = new ViewLeaderboardInteractor(habitGateway, presenter);

        HabitBuilder habitBuilder = new HabitBuilder();
        Habit habit = habitBuilder
                .setHabitName("Exercise")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Health")
                .setStreakCount(10)
                .setPriority(5)
                .setStatus(true)
                .build();

        habitGateway.addHabit("alice", habit);

        // Act
        interactor.execute(new ViewLeaderboardInputData());

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);

        List<Map<String, Object>> entries = presenter.successData.getLeaderboardEntries();
        assertEquals(1, entries.size());

        Map<String, Object> entry = entries.get(0);
        assertEquals("alice", entry.get("username"));
        assertEquals(10, entry.get("maxStreak"));
        assertEquals(1, entry.get("rank"));
    }

    @Test
    void execute_calculatesMaxStreakFromMultipleHabits() {
        // Arrange
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        ViewLeaderboardInteractor interactor = new ViewLeaderboardInteractor(habitGateway, presenter);

        // User with multiple habits: max streak should be 25
        Habit habit1 = new HabitBuilder()
                .setHabitName("Exercise")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Health")
                .setStreakCount(10)
                .setPriority(5)
                .setStatus(true)
                .build();

        Habit habit2 = new HabitBuilder()
                .setHabitName("Reading")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 20, 0))
                .setFrequency(1)
                .setHabitGroup("Learning")
                .setStreakCount(25)
                .setPriority(3)
                .setStatus(true)
                .build();

        Habit habit3 = new HabitBuilder()
                .setHabitName("Meditation")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 7, 0))
                .setFrequency(1)
                .setHabitGroup("Health")
                .setStreakCount(5)
                .setPriority(2)
                .setStatus(true)
                .build();

        habitGateway.addHabit("alice", habit1);
        habitGateway.addHabit("alice", habit2);
        habitGateway.addHabit("alice", habit3);

        // Act
        interactor.execute(new ViewLeaderboardInputData());

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);

        List<Map<String, Object>> entries = presenter.successData.getLeaderboardEntries();
        assertEquals(1, entries.size());

        Map<String, Object> entry = entries.get(0);
        assertEquals("alice", entry.get("username"));
        assertEquals(25, entry.get("maxStreak"));
    }

    @Test
    void execute_handlesUsersWithZeroStreak() {
        // Arrange
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        ViewLeaderboardInteractor interactor = new ViewLeaderboardInteractor(habitGateway, presenter);

        Habit habit1 = new HabitBuilder()
                .setHabitName("Exercise")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Health")
                .setStreakCount(0)
                .setPriority(5)
                .setStatus(false)
                .build();

        Habit habit2 = new HabitBuilder()
                .setHabitName("Reading")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 20, 0))
                .setFrequency(1)
                .setHabitGroup("Learning")
                .setStreakCount(10)
                .setPriority(3)
                .setStatus(true)
                .build();

        habitGateway.addHabit("alice", habit1);
        habitGateway.addHabit("bob", habit2);

        // Act
        interactor.execute(new ViewLeaderboardInputData());

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);

        List<Map<String, Object>> entries = presenter.successData.getLeaderboardEntries();
        assertEquals(2, entries.size());

        // bob should be first (streak 10), alice second (streak 0)
        Map<String, Object> firstEntry = entries.get(0);
        assertEquals("bob", firstEntry.get("username"));
        assertEquals(10, firstEntry.get("maxStreak"));

        Map<String, Object> secondEntry = entries.get(1);
        assertEquals("alice", secondEntry.get("username"));
        assertEquals(0, secondEntry.get("maxStreak"));
    }

    @Test
    void execute_handlesUsersWithSameMaxStreak() {
        // Arrange
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        ViewLeaderboardInteractor interactor = new ViewLeaderboardInteractor(habitGateway, presenter);

        Habit habit1 = new HabitBuilder()
                .setHabitName("Exercise")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Health")
                .setStreakCount(15)
                .setPriority(5)
                .setStatus(true)
                .build();

        Habit habit2 = new HabitBuilder()
                .setHabitName("Reading")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 20, 0))
                .setFrequency(1)
                .setHabitGroup("Learning")
                .setStreakCount(15)
                .setPriority(3)
                .setStatus(true)
                .build();

        habitGateway.addHabit("alice", habit1);
        habitGateway.addHabit("bob", habit2);

        // Act
        interactor.execute(new ViewLeaderboardInputData());

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);

        List<Map<String, Object>> entries = presenter.successData.getLeaderboardEntries();
        assertEquals(2, entries.size());

        // Both users should have streak 15
        for (Map<String, Object> entry : entries) {
            assertEquals(15, entry.get("maxStreak"));
            assertTrue(entry.get("username").equals("alice") || entry.get("username").equals("bob"));
        }
    }

    @Test
    void execute_handlesDataAccessException() {
        // Arrange
        HabitGateway throwingGateway = new HabitGateway() {
            @Override
            public String addHabit(String userId, Habit habit) {
                return "Habit Added Successfully";
            }

            @Override
            public java.util.ArrayList<Habit> fetchHabits(String userId) {
                return new java.util.ArrayList<>();
            }

            @Override
            public boolean deleteHabit(String userId, Habit habit) {
                return false;
            }

            @Override
            public java.util.Map<String, java.util.List<Habit>> getAllUsersWithHabits() {
                throw new RuntimeException("Database connection failed");
            }

            @Override
            public java.util.List<String> getAllUsernames() {
                return new java.util.ArrayList<>();
            }

            @Override
            public java.util.List<Habit> getHabitsForUser(String username) {
                return new java.util.ArrayList<>();
            }
        };

        TestPresenter presenter = new TestPresenter();
        ViewLeaderboardInteractor interactor = new ViewLeaderboardInteractor(throwingGateway, presenter);

        // Act
        interactor.execute(new ViewLeaderboardInputData());

        // Assert
        assertNotNull(presenter.failMessage);
        assertTrue(presenter.failMessage.contains("Failed to load leaderboard"));
        assertTrue(presenter.failMessage.contains("Database connection failed"));
        assertNull(presenter.successData);
    }

    @Test
    void execute_ranksEntriesCorrectlyInDescendingOrder() {
        // Arrange
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        TestPresenter presenter = new TestPresenter();
        ViewLeaderboardInteractor interactor = new ViewLeaderboardInteractor(habitGateway, presenter);

        // Create users with streaks: 5, 20, 10, 30, 15
        habitGateway.addHabit("user1", new HabitBuilder()
                .setHabitName("Habit1")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Group1")
                .setStreakCount(5)
                .setPriority(1)
                .setStatus(true)
                .build());

        habitGateway.addHabit("user2", new HabitBuilder()
                .setHabitName("Habit2")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Group2")
                .setStreakCount(20)
                .setPriority(2)
                .setStatus(true)
                .build());

        habitGateway.addHabit("user3", new HabitBuilder()
                .setHabitName("Habit3")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Group3")
                .setStreakCount(10)
                .setPriority(3)
                .setStatus(true)
                .build());

        habitGateway.addHabit("user4", new HabitBuilder()
                .setHabitName("Habit4")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Group4")
                .setStreakCount(30)
                .setPriority(4)
                .setStatus(true)
                .build());

        habitGateway.addHabit("user5", new HabitBuilder()
                .setHabitName("Habit5")
                .setStartDateTime(LocalDateTime.of(2024, 1, 1, 6, 0))
                .setFrequency(1)
                .setHabitGroup("Group5")
                .setStreakCount(15)
                .setPriority(5)
                .setStatus(true)
                .build());

        // Act
        interactor.execute(new ViewLeaderboardInputData());

        // Assert
        assertNull(presenter.failMessage);
        assertNotNull(presenter.successData);

        List<Map<String, Object>> entries = presenter.successData.getLeaderboardEntries();
        assertEquals(5, entries.size());

        // Verify descending order: 30, 20, 15, 10, 5
        assertEquals(30, entries.get(0).get("maxStreak"));
        assertEquals(20, entries.get(1).get("maxStreak"));
        assertEquals(15, entries.get(2).get("maxStreak"));
        assertEquals(10, entries.get(3).get("maxStreak"));
        assertEquals(5, entries.get(4).get("maxStreak"));

        // Verify ranks are assigned correctly
        assertEquals(1, entries.get(0).get("rank"));
        assertEquals(2, entries.get(1).get("rank"));
        assertEquals(3, entries.get(2).get("rank"));
        assertEquals(4, entries.get(3).get("rank"));
        assertEquals(5, entries.get(4).get("rank"));
    }

    @Test
    void execute_handlesUserWithEmptyHabitsList() {
        // Arrange - Test the branch when habits list is empty

        // Add a user with an empty habits list (simulated by not adding any habits)
        // The getAllUsersWithHabits should return empty list for this user
        // Actually, we need to add a user entry with empty list
        // Since InMemoryHabitDataAccessObject doesn't support this directly,
        // we'll test the case where getAllUsersWithHabits returns a user with empty list
        HabitGateway customGateway = new HabitGateway() {
            @Override
            public String addHabit(String userId, Habit habit) {
                return "Habit Added Successfully";
            }

            @Override
            public java.util.ArrayList<Habit> fetchHabits(String userId) {
                return new java.util.ArrayList<>();
            }

            @Override
            public boolean deleteHabit(String userId, Habit habit) {
                return false;
            }

            @Override
            public java.util.Map<String, java.util.List<Habit>> getAllUsersWithHabits() {
                java.util.Map<String, java.util.List<Habit>> result = new java.util.HashMap<>();
                result.put("emptyUser", new java.util.ArrayList<>());
                return result;
            }

            @Override
            public java.util.List<String> getAllUsernames() {
                return new java.util.ArrayList<>();
            }

            @Override
            public java.util.List<Habit> getHabitsForUser(String username) {
                return new java.util.ArrayList<>();
            }
        };

        TestPresenter presenter2 = new TestPresenter();
        ViewLeaderboardInteractor interactor2 = new ViewLeaderboardInteractor(customGateway, presenter2);

        // Act
        interactor2.execute(new ViewLeaderboardInputData());

        // Assert - User with empty habits should have maxStreak of 0
        assertNull(presenter2.failMessage);
        assertNotNull(presenter2.successData);

        List<Map<String, Object>> entries = presenter2.successData.getLeaderboardEntries();
        assertEquals(1, entries.size());

        Map<String, Object> entry = entries.get(0);
        assertEquals("emptyUser", entry.get("username"));
        assertEquals(0, entry.get("maxStreak"));
        assertEquals(1, entry.get("rank"));
    }

    @Test
    void viewLeaderboardOutputData_getterReturnsCorrectData() {
        // Test ViewLeaderboardOutputData class directly
        List<Map<String, Object>> testEntries = new ArrayList<>();
        Map<String, Object> entry1 = new HashMap<>();
        entry1.put("username", "user1");
        entry1.put("maxStreak", 10);
        entry1.put("rank", 1);
        testEntries.add(entry1);

        ViewLeaderboardOutputData outputData = new ViewLeaderboardOutputData(testEntries);

        List<Map<String, Object>> retrievedEntries = outputData.getLeaderboardEntries();
        assertEquals(1, retrievedEntries.size());
        assertEquals("user1", retrievedEntries.get(0).get("username"));
        assertEquals(10, retrievedEntries.get(0).get("maxStreak"));
    }

    @Test
    void viewLeaderboardInputData_canBeInstantiated() {
        // Test ViewLeaderboardInputData class instantiation
        ViewLeaderboardInputData inputData = new ViewLeaderboardInputData();
        assertNotNull(inputData);
    }
}

