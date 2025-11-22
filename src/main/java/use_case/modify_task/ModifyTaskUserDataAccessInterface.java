package use_case.modify_task;

import entities.Task;

public interface ModifyTaskUserDataAccessInterface {
    /***
     * Saves the modified task to the system under the given user
     * @param userID
     * @param modifiedTask
     */
    String addTask(String userID, Task modifiedTask);

    boolean deleteTask(String userID, Task modifiedTask);
}
