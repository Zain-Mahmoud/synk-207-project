package use_case.delete_task;

public interface DeleteTaskUserDataAccess {

    /**
     * Delete the task with the given name for the given user.
     */
    void deleteTask(String username, String taskName);

    /**
     * Check whether the given user has a task with the given name.
     */
    boolean existsTaskByName(String username, String taskName);
}
