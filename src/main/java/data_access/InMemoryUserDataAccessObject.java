package data_access;

import entities.User;
import use_case.gateways.UserGateway;


import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of the DAO for storing user data. This implementation does
 * NOT persist data between runs of the program. This is mainly for writing unit tests
 */
public class InMemoryUserDataAccessObject implements UserGateway {

    private final Map<String, User> users = new HashMap<>();

    private String currentUsername;

    public boolean existsByName(String identifier) {
        return users.containsKey(identifier);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return false;
    }

    @Override
    public void save(User user) {

    }

    @Override
    public User getByUid(String userID) {
        return users.get(userID);
    }

    @Override
    public User getByName(String username) {
        return null;
    }

    @Override
    public boolean existsByUid(String uid) {
        return false;
    }

    @Override
    public void updateUser(String oldUserID, User newUser) {
        users.replace(oldUserID, newUser);
    }

    @Override
    public void deleteUser(String oldUserID){
        users.remove(oldUserID);

    }


}