package interface_adapter.update_profile;

/**
 * The state for the Update Profile View.
 */
public class UpdateProfileState {

    private String uid = "";
    private String username = "";
    private String password = "";
    private String avatarPath = "";

    private String usernameError;
    private String passwordError;
    private String avatarError;
    private String successMessage;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
    }

    public String getPasswordError() {return this.passwordError; }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }

    public String getAvatarError() {
        return avatarError;
    }

    public void setAvatarError(String avatarError) {
        this.avatarError = avatarError;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    @Override
    public String toString() {
        return "UpdateProfileState{"
                + "uid='" + uid + '\''
                + ", username='" + username + '\''
                + ", avatarPath='" + avatarPath + '\''
                + ", passwords='" + password + '\''
                + '}';
    }
}
