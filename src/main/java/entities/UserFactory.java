package entities;

/**
 * Factory for creating CommonUser objects.
 */
public class UserFactory {

    public User create(String uid, String username, String avatarPath, String password) {
        return new User(uid, username, avatarPath, password);
    }

    public User create(String name, String password) {
        return new User(name, password);
    }
}
