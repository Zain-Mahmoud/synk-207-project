package data_access;

import entities.Task;
import use_case.gateways.TaskGateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of the DAO for storing Task data.
 * This implementation does NOT persist data between runs of the program.
 * It is primarily used for testing purposes.
 */
public class InMemoryTaskDataAccessObject implements TaskGateway {

    private final Map<String, ArrayList<Task>> userTasks = new HashMap<>();

    public InMemoryTaskDataAccessObject() {
    }

    @Override
    public String addTask(String userId, Task task) {
        ArrayList<Task> tasksForUser = userTasks.computeIfAbsent(userId, key -> new ArrayList<>());

        // Check for duplicates before adding
        if (tasksForUser.contains(task)) {
            return "Error Adding Task: Task already exists";
        }

        tasksForUser.add(task);
        return "Task Added Successfully";
    }

    @Override
    public ArrayList<Task> fetchTasks(String userId) {
        // Return a new list to prevent outside modification of the internal map object
        ArrayList<Task> tasks = userTasks.get(userId);

        if (tasks == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(tasks);
    }

    @Override
    public boolean deleteTask(String userId, Task task) {
        ArrayList<Task> tasks = userTasks.get(userId);
        if (tasks == null) {
            return false;
        }

        return tasks.remove(task);
    }
}
