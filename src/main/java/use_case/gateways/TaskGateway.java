package use_case.gateways;

// CRUD GET (read), PUT (create and update), POST (create - if we don't have `id` or `uuid`), and DELETE (delete)

import entities.Task;

import java.util.ArrayList;

public interface TaskGateway {

    String addTask(String userId, Task task);

    ArrayList<Task> fetchTasks(String userId);

    boolean deleteTask(String userId, Task task);




}