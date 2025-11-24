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
    public boolean createUser(User user) {
        users.put(user.getName(), user);
        return true;
    }

    @Override
    public User getUserById(String userID) {
        return users.get(userID);
    }

    @Override
    public boolean updateUser(String oldUserID, User newUser) {
        for (User user : users.values()){
            if (user.getName().equals(oldUserID)){
                users.replace(oldUserID, newUser);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteUser(String oldUserID){
        if (!this.existsByName(oldUserID)) {
            return false;
        } else{
            users.remove(oldUserID);
            return true;
        }
    }


}