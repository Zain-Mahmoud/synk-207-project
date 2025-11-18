package entities;

/**
 * A simple implementation of the User interface.
 */
public class User {

    private final String uid;
    private final String username;
    private final String AvatarPath;
    private final String password;

    public User(String uid, String username, String AvatarPath, String password) {
        this.uid = uid;
        this.username = username;
        this.AvatarPath = AvatarPath;
        this.password = password;
    }

    public String getUid() {return uid; };

    public String getUsername() {
        return username;
    }

    public String getAvatarPath() {
        return AvatarPath;
    }

    public String getPassword() {
        return password;
    }

}
