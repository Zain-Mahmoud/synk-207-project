package use_case.gateways;

// CRUD GET (read), PUT (create and update), POST (create - if we don't have `id` or `uuid`), and DELETE (delete)

import entities.Habit;
import entities.Task;

import java.util.ArrayList;

public interface TaskHabitGateway {

    String addTask(String userId, Task task);

    ArrayList<Task> fetchTasks(String userId);

    boolean deleteTask(String userId, Task task);

    boolean deleteHabit(String userId, Habit habit);

    String addHabit(String userId, Habit habit);



}