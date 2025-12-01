package use_case.signup;

/**
 * Output Data for the Signup Use Case.
 */
public class SignupOutputData {

    private final String uid;
    private final String username;

    private final boolean useCaseFailed;

    public SignupOutputData(String uid, String username, boolean useCaseFailed) {
        this.uid = uid;
        this.username = username;
        this.useCaseFailed = useCaseFailed;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}
