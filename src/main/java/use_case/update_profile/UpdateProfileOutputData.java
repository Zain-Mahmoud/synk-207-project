package use_case.update_profile;

/**
 * Output Data for the UpdateProfile Use Case.
 */
public class UpdateProfileOutputData {
    private final String uid;
    private final String username;
    private final String avatarPath;
    private final String password;
    private final boolean useCaseFailed;

    public UpdateProfileOutputData(String uid,
                                   String username,
                                   String avatarPath,
                                   String password,
                                   boolean useCaseFailed) {
        this.uid = uid;
        this.username = username;
        this.avatarPath = avatarPath;
        this.password = password;
        this.useCaseFailed = useCaseFailed;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getPassword() {return password; }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}
