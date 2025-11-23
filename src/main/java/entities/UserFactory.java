package entities;
import java.util.UUID;
/**
 * Factory for creating CommonUser objects.
 */
public class UserFactory {

    public User create(String username, String password) {
        String uid = UUID.randomUUID().toString();
        System.out.println(uid);
        return new User(uid, username, null, password);
    }
}
