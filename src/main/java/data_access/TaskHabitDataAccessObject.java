package data_access;

import entities.Task;
import jdk.jshell.spi.ExecutionControl;
import use_case.gateways.TaskGateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Prototype in-memory persistence layer backed by a simple HashMap of user id to tasks.
 */
public class TaskHabitDataAccessObject implements TaskGateway {

	private final Map<String, ArrayList<Task>> userTasks = new HashMap<>();

	@Override
    public String addTask(String userId, Task task) {
        // Compute if absent to initialize user's task list, if it exists return it

		ArrayList<Task> tasksForUser = userTasks.computeIfAbsent(userId, key -> new ArrayList<>());

        try {
            tasksForUser.add(task);
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

