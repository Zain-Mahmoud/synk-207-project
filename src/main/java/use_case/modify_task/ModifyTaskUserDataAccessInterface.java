package use_case.modify_task;

import entities.Task;

public interface ModifyTaskUserDataAccessInterface {
    /***
     * Saves the modified task to the system under the given user
     * @param userID
     * @param modifiedTask
     */
    void saveTask(String userID, Task modifiedTask);


}
