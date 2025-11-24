package data_access;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Habit;
import entities.HabitBuilder;
import use_case.gateways.HabitGateway;
import use_case.view_leaderboard.ViewLeaderboardUserDataAccessInterface;

/**
 * Persistence layer for Habits backed by a CSV file.
 * Implements Create, Update, Remove, Read (Fetch) operations for habits.
 */
public class HabitDataAccessObject implements HabitGateway, ViewLeaderboardUserDataAccessInterface {

    private static final String HABIT_HEADER = "username,habitName,streakCount,startDateTime,frequency,habitGroup,priority,status";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final File habitCsvFile;
    private final Map<String, ArrayList<Habit>> userHabits = new HashMap<>();

    public HabitDataAccessObject() {
        this(Path.of("habits.csv"));
    }

    public HabitDataAccessObject(Path habitCsvPath) {
        this.habitCsvFile = habitCsvPath.toFile();
        initializeFileIfNeeded(habitCsvFile, HABIT_HEADER);
        loadHabitsFromCsv();
    }

    private void initializeFileIfNeeded(File csvFile, String header) {
        if (csvFile.exists()) {
            return;
        }
        try {
            if (csvFile.getParentFile() != null) {
                csvFile.getParentFile().mkdirs();
            }
            if (csvFile.createNewFile()) {
                writeHeader(csvFile, header);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize CSV file: " + csvFile.getName(), e);
        }
    }

    private void writeHeader(File csvFile, String header) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write(header);
            writer.newLine();
        }
    }

    // ========== HABIT PERSISTENCE METHODS ==========

    private void loadHabitsFromCsv() {
        userHabits.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(habitCsvFile))) {
            final String headerLine = reader.readLine();
            if (headerLine == null) {
                writeHeader(habitCsvFile, HABIT_HEADER);
                return;
            }
            if (!HABIT_HEADER.equals(headerLine)) {
                throw new IllegalStateException("Unexpected header in habits CSV");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                final String[] columns = line.split(",", -1);
                if (columns.length < 8) {
                    continue;
                }
                final String username = columns[0];
                final String habitName = columns[1];
                final int streakCount = columns[2].isBlank() ? 0 : Integer.parseInt(columns[2]);
                final String startDateTimeRaw = columns[3];
                final String frequencyRaw = columns[4];
                final String habitGroup = columns[5];
                final int priority = columns[6].isBlank() ? 0 : Integer.parseInt(columns[6]);
                final boolean status = Boolean.parseBoolean(columns[7]);

                LocalDateTime startDateTime = null;
                if (!startDateTimeRaw.isBlank()) {
                    startDateTime = LocalDateTime.parse(startDateTimeRaw, DATE_FORMATTER);
                }
                LocalDateTime frequency = null;
                if (!frequencyRaw.isBlank()) {
                    frequency = LocalDateTime.parse(frequencyRaw, DATE_FORMATTER);
                }

                Habit habit = new HabitBuilder()
                        .setHabitName(habitName)
                        .setStartDateTime(startDateTime)
                        .setFrequency(frequency)
                        .setHabitGroup(habitGroup)
                        .setStreakCount(streakCount)
                        .setPriority(priority)
                        .setStatus(status)
                        .build();

                userHabits.computeIfAbsent(username, key -> new ArrayList<>()).add(habit);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load habits from CSV", e);
        }
    }

    private void persistHabitsToCsv() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(habitCsvFile))) {
            writer.write(HABIT_HEADER);
            writer.newLine();
            for (Map.Entry<String, ArrayList<Habit>> entry : userHabits.entrySet()) {
                final String username = entry.getKey();
                for (Habit habit : entry.getValue()) {
                    final String startDateTime = habit.getStartDateTime() == null ? "" : DATE_FORMATTER.format(habit.getStartDateTime());
                    final String frequency = habit.getFrequency() == null ? "" : DATE_FORMATTER.format(habit.getFrequency());
                    final String line = String.join(",",
                            username,
                            safe(habit.getName()),
                            Integer.toString(habit.getStreakCount()),
                            startDateTime,
                            frequency,
                            safe(habit.getHabitGroup()),
                            Integer.toString(habit.getPriority()),
                            Boolean.toString(habit.getStatus())
                    );
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist habits to CSV", e);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    // ========== HABIT GATEWAY METHODS (Stand-in) ==========

    public String addHabit(String userId, Habit habit) {
        ArrayList<Habit> habitsForUser = userHabits.computeIfAbsent(userId, key -> new ArrayList<>());

        try {
            habitsForUser.add(habit);
            persistHabitsToCsv();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Adding Habit";
        }
        return "Habit Added Successfully";
    }

    public ArrayList<Habit> fetchHabits(String userId) {
        ArrayList<Habit> habits = userHabits.get(userId);

        if (habits == null) {
            return new ArrayList<>();
        }

        return habits;
    }

    public boolean deleteHabit(String userId, Habit habit) {
        ArrayList<Habit> habits = userHabits.get(userId);
        if (habits == null) {
            return false;
        }

        boolean removed = habits.remove(habit);
        if (removed && !habits.contains(habit)) {
            persistHabitsToCsv();
            return removed;
        }
        return false;
    }

    // ========== VIEW LEADERBOARD DATA ACCESS INTERFACE METHODS ==========

    @Override
    public List<String> getAllUsernames() {
        return new ArrayList<>(userHabits.keySet());
    }

    @Override
    public List<Habit> getHabitsForUser(String username) {
        ArrayList<Habit> habits = userHabits.get(username);
        if (habits == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(habits);
    }

    @Override
    public Map<String, List<Habit>> getAllUsersWithHabits() {
        Map<String, List<Habit>> result = new HashMap<>();
        for (Map.Entry<String, ArrayList<Habit>> entry : userHabits.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }
}