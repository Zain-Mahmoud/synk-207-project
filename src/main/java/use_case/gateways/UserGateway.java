package use_case.gateways;

import entities.User;

public interface UserGateway {
    void createUser(User user);

    User getUserById(String userId);

    void updateUser(String oldUserID, User user);

    void deleteUser(String userId);
}