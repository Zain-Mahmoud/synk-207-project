package use_case.modify_task;

import entities.Task;

public interface ModifyTaskUserDataAccessInterface {
    /***
     * Updates the system to modify this user's task
     * @param newTask
     */
    void modifyTask(Task newTask);
}
