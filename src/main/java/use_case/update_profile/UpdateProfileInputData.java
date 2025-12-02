package use_case.update_profile;

/**
 * The Input Data for the UpdateProfile Use Case.
 */
public class UpdateProfileInputData {
    private final String uid;
    private final String newUsername;
    private final String newAvatarPath;
    private final String newPassword;

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

    public String getNewPassword() {
        return newPassword;
    }
}

