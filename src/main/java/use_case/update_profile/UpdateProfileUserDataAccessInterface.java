package use_case.update_profile;

import entities.User;

/**
 * DAO for the UpdateProfile Use Case.
 */
public interface UpdateProfileUserDataAccessInterface {

    /**
     * Checks if the given UID exists.
     * @param uid the UID to look for
     * @return true if a user with the given username exists; false otherwise
     */
    boolean existsByUid(String uid);

    /**
     * Gets the user with a specific UID.
     * @param uid the username to look for
     * @return user with the UID provided
     */
    User getByUid(String uid);

    /**
     * Checks if the username is taken.
     * @param username the username to compare
     * @return true if the username is taken; false otherwise
     */
    boolean isUsernameTaken(String username);

    /**
     * Saves the user.
     * @param user the user to save
     */
    void save(User user);
}
