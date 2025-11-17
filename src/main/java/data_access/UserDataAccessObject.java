package data_access;

import entities.User;
import use_case.gateways.UserGateway;

/**
 * UserDataAccessObject (LOCALLY)
 * <p>
 * Short description:
 * - Data access layer responsible for storing and retrieving user
 * information (profiles, settings, and authentication tokens).
 * <p>
 * Responsibilities / contract:
 * - Inputs: user entities or credentials for create/update/lookup operations.
 * - Outputs: User objects, authentication tokens, or boolean status for
 * changes.
 * - Error modes: credential validation failures, missing users, or
 * persistence errors. Callers should validate inputs and handle errors or
 * null returns accordingly.
 * <p>
 * Notes:
 * - This class should encapsulate user persistence details and expose simple
 * methods such as createUser, getUserById, updateUser, and deleteUser.
 * *** IMPORTANT***
 * - This assumes that we have seperate logins for Google Calendar and User accounts.
 */
public class UserDataAccessObject implements UserGateway {
    /**
     * @param user
     * @return
     */
    @Override
    public boolean createUser(User user) {
        return false;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public User getUserById(String userId) {
        return null;
    }

    /**
     * @param user
     * @return
     */
    @Override
    public boolean updateUser(User user) {
        return false;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public boolean deleteUser(String userId) {
        return false;
    }
}
