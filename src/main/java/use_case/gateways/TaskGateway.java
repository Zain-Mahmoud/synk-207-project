package use_case.gateways;

// CRUD GET (read), PUT (create and update), POST (create - if we don't have `id` or `uuid`), and DELETE (delete)

import entities.Task;

import java.util.ArrayList;

public interface TaskGateway {
import entities.Task;

import java.util.List;

/**
 * Minimal CRUD style contract for storing tasks per user during early prototyping.
 */
public interface TaskGateway {

    String addTask(String userId, Task task);

    ArrayList<Task> fetchTasks(String userId);

    boolean deleteTask(String userId, Task task);


	/**
	 * Persist the supplied task for the given user, creating a new task list on demand.
     *
     * @return The ID of the newly created task and if Successful
	 */
	String addTask(String userId, Task task);

	/**
	 * Retrieve all tasks currently known for the user. Returns an empty list when none exist.
     *
     * @return List of Tasks (OBJECTS) for the user.
	 */
	List<Task> fetchTasks(String userId);

	// /**
	//  * Replace an existing task that {@link Task#equals(Object)} the provided previous instance.
	//  *
	//  * @return true when a matching task was updated; false when no match was found.
	//  */
	// boolean updateTask(String userId, Task previous, Task updated);

    // NOT SURE IF WE NEED UPDATE FUNCTIONALITY YET

	/**
	 * Remove the matching task from storage.
	 *
	 * @return true when the task was removed; false when no matching task existed.
	 */
	boolean deleteTask(String userId, Task task);
}