package data_access;// package data_access;

import entities.Habit;
import entities.HabitBuilder;
import entities.Task;
import entities.TaskBuilder;
import use_case.gateways.TaskHabitGateway;

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
import java.util.Map;

/**
 * Persistence layer for both Tasks and Habits backed by CSV files.
 * Create, Update, Remove, Read (Fetch) operations for both tasks and habits.
 */
public class TaskHabitDataAccessObject implements TaskHabitGateway {

    private static final String TASK_HEADER = "userId,taskName,description,startTime,deadline,taskGroup,status,priority";
    private static final String HABIT_HEADER = "username,habitName,streakCount,startDateTime,frequency,habitGroup,priority,status";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final File taskCsvFile;
    private final File habitCsvFile;
    private final Map<String, ArrayList<Task>> userTasks = new HashMap<>();
    private final Map<String, ArrayList<Habit>> userHabits = new HashMap<>();

    public TaskHabitDataAccessObject() {
        this(Path.of("tasks.csv"), Path.of("habits.csv"));
    }



    public TaskHabitDataAccessObject(Path taskCsvPath, Path habitCsvPath) {
        this.taskCsvFile = taskCsvPath.toFile();
        this.habitCsvFile = habitCsvPath.toFile();
        initializeFileIfNeeded(taskCsvFile, TASK_HEADER);
        initializeFileIfNeeded(habitCsvFile, HABIT_HEADER);
        loadTasksFromCsv();
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

    // ========== TASK METHODS ==========

    private void loadTasksFromCsv() {
        userTasks.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(taskCsvFile))) {
            final String headerLine = reader.readLine();
            if (headerLine == null) {
                writeHeader(taskCsvFile, TASK_HEADER);
                return;
            }
            if (!TASK_HEADER.equals(headerLine)) {
                throw new IllegalStateException("Unexpected header in tasks CSV");
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
                final String userId = columns[0];
                final String taskName = columns[1];
                final String description = columns[2];
                final String startTimeRaw = columns[3];
                final String deadlineRaw = columns[4];
                final String taskGroup = columns[5];
                final boolean status = Boolean.parseBoolean(columns[6]);
                final int priority = columns[7].isBlank() ? 0 : Integer.parseInt(columns[7]);

                LocalDateTime startTime = null;
                if (!startTimeRaw.isBlank()) {
                    startTime = LocalDateTime.parse(startTimeRaw, DATE_FORMATTER);
                }
                LocalDateTime deadline = null;
                if (!deadlineRaw.isBlank()) {
                    deadline = LocalDateTime.parse(deadlineRaw, DATE_FORMATTER);
                }

                Task task = new TaskBuilder()
                        .setTaskName(taskName)
                        .setDescription(description)
                        .setDeadline(deadline)
                        .setTaskGroup(taskGroup)
                        .setStatus(status)
                        .setPriority(priority)
                        .build();

                userTasks.computeIfAbsent(userId, key -> new ArrayList<>()).add(task);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tasks from CSV", e);
        }
    }

    private void persistTasksToCsv() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(taskCsvFile))) {
            writer.write(TASK_HEADER);
            writer.newLine();
            for (Map.Entry<String, ArrayList<Task>> entry : userTasks.entrySet()) {
                final String userId = entry.getKey();
                for (Task task : entry.getValue()) {
                    final String startTime = task.getStartTime() == null ? "" : DATE_FORMATTER.format(task.getStartTime());
                    final String deadline = task.getDeadline() == null ? "" : DATE_FORMATTER.format(task.getDeadline());
                    final String line = String.join(",",
                            userId,
                            safe(task.getName()),
                            safe(task.getDescription()),
                            startTime,
                            deadline,
                            safe(task.getTaskGroup()),
                            Boolean.toString(task.getStatus()),
                            Integer.toString(task.getPriority())
                    );
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist tasks to CSV", e);
        }
    }

    // ========== HABIT METHODS ==========

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

    // ========== UTILITY METHODS ==========

    private String safe(String value) {
        return value == null ? "" : value;
    }

    // ========== TASK GATEWAY IMPLEMENTATION ==========

    @Override
    public String addTask(String userId, Task task) {
        ArrayList<Task> tasksForUser = userTasks.computeIfAbsent(userId, key -> new ArrayList<>());

        try {
            tasksForUser.add(task);
            persistTasksToCsv();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Adding Task";
        }
        return "Task Added Successfully";
    }

    @Override
    public ArrayList<Task> fetchTasks(String userId) {
        ArrayList<Task> tasks = userTasks.get(userId);

        if (tasks == null) {
            return new ArrayList<>();
        }

        return tasks;
    }

    @Override
    public boolean deleteTask(String userId, Task task) {
        ArrayList<Task> tasks = userTasks.get(userId);
        if (tasks == null) {
            return false;
        }

        boolean removed = tasks.remove(task);
        if (removed && !(tasks.contains(task))) {
            persistTasksToCsv();
            return removed;
        }
        return false;
    }

    // ========== HABIT GATEWAY METHODS ==========

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
}
