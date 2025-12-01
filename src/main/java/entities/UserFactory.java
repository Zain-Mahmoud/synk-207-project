package entities;
import java.util.UUID;
/**
 * Factory for creating CommonUser objects.
 */
public class UserFactory {

    public User create(String username, String password) {
        String uid = UUID.randomUUID().toString();
        return new User(uid, username, null, password);
    }

    public User create(String uid, String username, String avatarPath, String password) {
        return new User(uid, username, avatarPath, password);
    }
}
