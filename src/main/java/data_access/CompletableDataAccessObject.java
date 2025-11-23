package data_access;// package data_access;

import entities.Completable;

import entities.TaskBuilder;
import use_case.gateways.CompletableGateway;

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
public class CompletableDataAccessObject implements CompletableGateway {

    private static final String HEADER = "userId,completableName,description,startTime,deadline,completableGroup,status,priority";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final File csvFile;
    private final Map<String, ArrayList<Completable>> completable = new HashMap<>();

    public CompletableDataAccessObject() {
        this(Path.of("completable.csv"));
    }

    public CompletableDataAccessObject(Path csvPath) {
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
            throw new RuntimeException("Unable to initialize completable CSV", e);
        }
    }

    private void writeHeader() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write(HEADER);
            writer.newLine();
        }
    }

    private void loadFromCsv() {
        completable.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            final String headerLine = reader.readLine();
            if (headerLine == null) {
                writeHeader();
                return;
            }
            if (!HEADER.equals(headerLine)) {
                throw new IllegalStateException("Unexpected header in completable CSV");
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
                final String completableName = columns[1];
                final String description = columns[2];
                final String startTimeRaw = columns[3];
                final String deadlineRaw = columns[4];
                final String completableGroup = columns[5];
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

                Completable completable = new TaskBuilder()
                        .setTaskName(completableName)
                        .setDescription(description)
                        // WE NEED A START TIME FIELD IN THE BUILDER
                        //.setStartTime(startTime)
                        .setDeadline(deadline)
                        .setTaskGroup(completableGroup)
                        .setStatus(status)
                        .setPriority(priority)
                        .build();

                this.completable.computeIfAbsent(userId, key -> new ArrayList<>()).add(completable);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load completable from CSV", e);
        }
    }

    private void persistToCsv() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write(HEADER);
            writer.newLine();
            for (Map.Entry<String, ArrayList<Completable>> entry : completable.entrySet()) {
                final String userId = entry.getKey();
                for (Completable completable : entry.getValue()) {
                    final String startTime = completable.getStartTime() == null ? "" : DATE_FORMATTER.format(completable.getStartTime());
                    final String deadline = completable.getDeadline() == null ? "" : DATE_FORMATTER.format(completable.getDeadline());
                    final String line = String.join(",",
                            userId,
                            safe(completable.getName()),
                            safe(completable.getDescription()),
                            startTime,
                            deadline,
                            safe(completable.getTaskGroup()),
                            Boolean.toString(completable.getStatus()),
                            Integer.toString(completable.getPriority())
                    );
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist completable to CSV", e);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String addCompletable(String userId, Completable completable) {
        // Compute if absent to initialize user's task list, if it exists return it

        ArrayList<Completable> tasksForUser = this.completable.computeIfAbsent(userId, key -> new ArrayList<>());

        try {
            tasksForUser.add(completable);
            persistToCsv();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Adding Task";
        }
        return "Task Added Successfully";
    }

    @Override
    public ArrayList<Completable> fetchCompletable(String userId) {
        ArrayList<Completable> completable = this.completable.get(userId);

        if (completable == null) {
            // Returns Empty List if no tasks for user
            return new ArrayList<>();
        }

        return completable;
    }


    @Override
    public boolean deleteCompletable(String userId, Completable Completable) {
        ArrayList<Completable> tasks = completable.get(userId);
        if (tasks == null) {
            return false;
        }

        boolean removed = tasks.remove(Completable);
        if (removed && !(tasks.contains(Completable))) {
            persistToCsv();
            return removed;
        }
        return false;
    }
}