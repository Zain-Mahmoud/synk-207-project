package use_case.gateways;

// CRUD GET (read), PUT (create and update), POST (create - if we don't have `id` or `uuid`), and DELETE (delete)

import entities.Completable;

import java.util.ArrayList;

public interface CompletableGateway {

    String addTask(String userId, Completable Completable);

    ArrayList<Completable> fetchTasks(String userId);

    boolean deleteTask(String userId, Completable completable);


}