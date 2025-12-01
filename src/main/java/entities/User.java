package entities;


/**
 * A simple implementation of the User interface.
 */
public class User {

    private final String uid;
    private String username;
    private String avatarpath;
    private String password;

    public User(String uid, String username, String password) {
        this.uid = uid;
        this.username = username;
        this.avatarpath = null;
        this.password = password;
    }

    public User(String uid, String username, String avatarpath, String password) {
        this.uid = uid;
        this.username = username;
        this.avatarpath = avatarpath;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarPath() {
        return avatarpath;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarPath(String url) {
        this.avatarpath = url;
    }

    public void setPassword(String password) {this.password = password;}

}


