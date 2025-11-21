// package data_access;

import entities.Task;
import entities.TaskBuilder;
import jdk.jshell.spi.ExecutionControl;
import use_case.gateways.TaskGateway;

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
 * Prototype in-memory persistence layer backed by a simple HashMap of user id to tasks.
 * Create, Update, Remove, Read (Fetch) :
 */
public static class TaskHabitDataAccessObject implements TaskGateway {

    private static final String HEADER = "userId,taskName,description,startTime,deadline,taskGroup,status,priority";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final File csvFile;
    private final Map<String, ArrayList<Task>> userTasks = new HashMap<>();

    public TaskHabitDataAccessObject() {
        this(Path.of("tasks.csv"));
    }

    public TaskHabitDataAccessObject(Path csvPath) {
        this.csvFile = csvPath.toFile();
        initializeFileIfNeeded();
        loadFromCsv();
    }

    private void initializeFileIfNeeded() {
        if (csvFile.exists()) {
            return;
        }
        try {
            csvFile.getParentFile(); // parent may be null for relative paths
            if (csvFile.getParentFile() != null) {
                csvFile.getParentFile().mkdirs();
            }
            if (csvFile.createNewFile()) {
                writeHeader();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize tasks CSV", e);
        }
    }

    private void writeHeader() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write(HEADER);
            writer.newLine();
        }
    }

    private void loadFromCsv() {
        userTasks.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            final String headerLine = reader.readLine();
            if (headerLine == null) {
                writeHeader();
                return;
            }
            if (!HEADER.equals(headerLine)) {
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
                        // WE NEED A START TIME FIELD IN THE BUILDER
                       //.setStartTime(startTime)
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

    private void persistToCsv() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write(HEADER);
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

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String addTask(String userId, Task task) {
        // Compute if absent to initialize user's task list, if it exists return it

        ArrayList<Task> tasksForUser = userTasks.computeIfAbsent(userId, key -> new ArrayList<>());

        try {
            tasksForUser.add(task);
            persistToCsv();
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
            // Returns Empty List if no tasks for user
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
            persistToCsv();
            return removed;
        }
        return false;
    }

    /**
     * @param userId
     * @param task
     * @return
     */

    // Just testing Not implemented exception, I remember there was an easy way to do it in Java,
    // but I forgor
    @Override
    public boolean updateTask(String userId, Task task) {
        try {
            throw new ExecutionControl.NotImplementedException("TODO");
        } catch (ExecutionControl.NotImplementedException e) {
            throw new RuntimeException(e);
        }
    }
}

// THIS IS TO TEST IT


void main() {
    TaskHabitDataAccessObject dao = new TaskHabitDataAccessObject();

    dao.addTask("1", new TaskBuilder().setTaskName("Task 1").build());


    System.out.println(dao.fetchTasks("1"));
}
