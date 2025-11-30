package use_case.gateways;

import entities.User;

public interface UserGateway {

    void updateUser(String oldUserID, User user);

    void deleteUser(String userId);

    User getByUid(String uid);

    User getByName(String username);

    boolean existsByUid(String uid);

    boolean existsByName(String identifier);

    boolean isUsernameTaken(String username);

    void save(User user);
}