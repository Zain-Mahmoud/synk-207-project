package use_case.update_profile;

/**
 * The Input Data for the UpdateProfile Use Case.
 */
public class UpdateProfileInputData {
    private final String uid; //UID of the current user
    private final String newUsername; //new username for current user
    private final String newAvatarPath; //new avatar Path (URL) for the current user
    private final String newPassword; //new password for the current user

    public UpdateProfileInputData(String uid,
                                  String newUsername,
                                  String newAvatarPath,
                                  String newPassword) {
        this.uid = uid;
        this.newUsername = newUsername;
        this.newAvatarPath = newAvatarPath;
        this.newPassword = newPassword;
    }

    public String getUid() {
        return uid;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public String getNewAvatarPath() {
        return newAvatarPath;
    }

    public String getNewPassword() {return newPassword; }
}

