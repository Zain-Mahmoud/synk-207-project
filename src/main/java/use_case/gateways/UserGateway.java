package use_case.gateways;

import entities.User;

public interface UserGateway {
    boolean createUser(User user);

    User getUserById(String userId);

    boolean updateUser(User user);

    boolean deleteUser(String userId);
}