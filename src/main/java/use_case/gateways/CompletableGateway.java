package use_case.gateways;

// CRUD GET (read), PUT (create and update), POST (create - if we don't have `id` or `uuid`), and DELETE (delete)

import entities.Completable;

import java.util.ArrayList;

public interface CompletableGateway {

    String addCompletable(String userId, Completable Completable);

    ArrayList<Completable> fetchCompletable(String userId);

    boolean deleteCompletable(String userId, Completable completable);


}