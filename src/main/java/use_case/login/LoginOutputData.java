package use_case.login;

/**
 * Output Data for the Login Use Case.
 */
public class LoginOutputData {

    private final String uid;
    private final String username;
    private final boolean useCaseFailed;

    public LoginOutputData(String uid, String username, boolean useCaseFailed) {
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

}
