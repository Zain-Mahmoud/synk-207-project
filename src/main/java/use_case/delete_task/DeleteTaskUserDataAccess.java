package use_case.delete_task;

public interface DeleteTaskUserDataAccess {

    void deleteTask(String username, String taskName);

    boolean existsTaskByName(String username, String taskName);
}
