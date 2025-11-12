package use_case.gateways;

import entities.Task;

import java.util.List;

/**
 * Minimal CRUD style contract for storing tasks per user during early prototyping.
 */
public interface TaskGateway {

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