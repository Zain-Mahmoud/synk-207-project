package data_access;

import entities.Habit;
import entities.HabitBuilder;
import use_case.gateways.TaskGateway;
import use_case.view_leaderboard.ViewLeaderboardUserDataAccessInterface;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * TaskHabitDataAccessObject (LOCALLY)
 *
 * Short description:
 * - Data access layer for persisting and retrieving Task and Habit entities.
 *
 * Responsibilities / contract:
 * - Inputs: domain entities like Task and Habit (or DTOs/IDs) for create,
 *   update, delete, and query operations.
 * - Outputs: persisted entities, collections of entities, or boolean/status
 *   indicators for write operations.
 * - Error modes: persistence failures, validation errors, or missing records.
 *   Callers should handle these by checking return values or catching
 *   exceptions depending on implementation.
 *
 * Notes:
 * - Typical methods: addTask/addHabit, updateTask/updateHabit,
 *   deleteTask/deleteHabit, findTasksByDate, and methods to manage streaks.
 */
public class TaskHabitDataAccessObject implements TaskGateway, ViewLeaderboardUserDataAccessInterface {

    private static final String HEADER = "username,habitName,streakCount,startDateTime,frequency,habitGroup,priority,status";

    private final File habitsCsvFile;
    private final Map<String, Integer> headers = new LinkedHashMap<>();
    private final Map<String, List<Habit>> userHabitsMap = new HashMap<>();

    public TaskHabitDataAccessObject(String csvPath) throws IOException {
        habitsCsvFile = new File(csvPath);
        
        // Initialize headers
        headers.put("username", 0);
        headers.put("habitName", 1);
        headers.put("streakCount", 2);
        headers.put("startDateTime", 3);
        headers.put("frequency", 4);
        headers.put("habitGroup", 5);
        headers.put("priority", 6);
        headers.put("status", 7);

        if (!habitsCsvFile.exists() || habitsCsvFile.length() == 0) {
            // Create empty file with header
            save();
        } else {
            loadHabits();
        }
    }

    private void loadHabits() throws IOException {
        userHabitsMap.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(habitsCsvFile))) {
            final String header = reader.readLine();
            
            if (header == null || !header.equals(HEADER)) {
                throw new RuntimeException(String.format("header should be%n: %s%n but was:%n%s", HEADER, header));
            }

            String row;
            while ((row = reader.readLine()) != null) {
                if (row.trim().isEmpty()) {
                    continue;
                }
                
                final String[] col = row.split(",");
                if (col.length < headers.size()) {
                    continue; // Skip malformed rows
                }

                try {
                    final String username = col[headers.get("username")].trim();
                    final String habitName = col[headers.get("habitName")].trim();
                    final int streakCount = Integer.parseInt(col[headers.get("streakCount")].trim());
                    final String startDateTimeStr = col[headers.get("startDateTime")].trim();
                    final String frequencyStr = col.length > headers.get("frequency") && !col[headers.get("frequency")].trim().isEmpty() 
                            ? col[headers.get("frequency")].trim() : null;
                    final String habitGroup = col.length > headers.get("habitGroup") && !col[headers.get("habitGroup")].trim().isEmpty()
                            ? col[headers.get("habitGroup")].trim() : null;
                    final int priority = col.length > headers.get("priority") && !col[headers.get("priority")].trim().isEmpty()
                            ? Integer.parseInt(col[headers.get("priority")].trim()) : 0;
                    final boolean status = col.length > headers.get("status") && !col[headers.get("status")].trim().isEmpty()
                            ? Boolean.parseBoolean(col[headers.get("status")].trim()) : false;

                    LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr);
                    LocalDateTime frequency = frequencyStr != null ? LocalDateTime.parse(frequencyStr) : null;

                    Habit habit = new HabitBuilder()
                            .setHabitName(habitName)
                            .setStartDateTime(startDateTime)
                            .setFrequency(frequency)
                            .setHabitGroup(habitGroup)
                            .setStreakCount(streakCount)
                            .setPriority(priority)
                            .setStatus(status)
                            .build();

                    userHabitsMap.computeIfAbsent(username, k -> new ArrayList<>()).add(habit);
                } catch (Exception e) {
                    // Skip malformed rows
                    System.err.println("Error parsing habit row: " + row + " - " + e.getMessage());
                }
            }
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(habitsCsvFile))) {
            writer.write(HEADER);
            writer.newLine();

            for (Map.Entry<String, List<Habit>> entry : userHabitsMap.entrySet()) {
                String username = entry.getKey();
                for (Habit habit : entry.getValue()) {
                    final String line = String.format("%s,%s,%d,%s,%s,%s,%d,%s",
                            username,
                            habit.getHabitName(),
                            habit.getStreakCount(),
                            habit.getStartDateTime(),
                            habit.getFrequency() != null ? habit.getFrequency().toString() : "",
                            habit.getHabitGroup() != null ? habit.getHabitGroup() : "",
                            habit.getPriority(),
                            habit.getStatus());
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getAllUsernames() {
        return new ArrayList<>(userHabitsMap.keySet());
    }

    @Override
    public List<Habit> getHabitsForUser(String username) {
        return new ArrayList<>(userHabitsMap.getOrDefault(username, new ArrayList<>()));
    }

    @Override
    public Map<String, List<Habit>> getAllUsersWithHabits() {
        Map<String, List<Habit>> result = new HashMap<>();
        for (Map.Entry<String, List<Habit>> entry : userHabitsMap.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }
}
