package use_case.update_profile;

import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import use_case.gateways.UserGateway;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UpdateProfileInteractor, focusing on the business logic
 * and ensuring proper interaction with the UserGateway (Data Access) and
 * UpdateProfileOutputBoundary (Presenter).
 */
public class UpdateProfileInteractorTest {

    static class MockUser extends User {
        private String username;
        private String avatarPath;
        private String password;

        public MockUser(String uid, String username, String avatarPath, String password) {
            super(uid, username, avatarPath, password);
            this.username = username;
            this.avatarPath = avatarPath;
            this.password = password;
        }

        @Override
        public String getUsername() { return username; }
        @Override
        public void setUsername(String username) { this.username = username; }
        @Override
        public String getAvatarPath() { return avatarPath; }
        @Override
        public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
        @Override
        public String getPassword() { return password; }
        @Override
        public void setPassword(String password) { this.password = password; }
    }

    public interface UpdateProfileOutputBoundary {
        void prepareSuccessView(UpdateProfileOutputData outputData);
        void prepareFailView(String error);
    }
    public interface UserGateway {
        boolean existsByUid(String uid);
        User getByUid(String uid);
        boolean isUsernameTaken(String username);
        void save(User user);
    }

    @Mock
    private UserGateway mockDataAccess;

    @Mock
    private UpdateProfileOutputBoundary mockPresenter;

    @InjectMocks
    private UpdateProfileInteractor interactor;

    private MockUser existingUser;
    private final String UID = "test-user-123";
    private final String OLD_USERNAME = "OldName";
    private final String OLD_PASSWORD = "longpassword";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        existingUser = new MockUser(UID, OLD_USERNAME, "/old/avatar.png", OLD_PASSWORD);

        when(mockDataAccess.existsByUid(UID)).thenReturn(true);
        when(mockDataAccess.getByUid(UID)).thenReturn(existingUser);
    }

    @Test
    void execute_SuccessfulUpdateAllFields_ShouldSaveAndPresentSuccess() {
        String newUsername = "NewName";
        String newAvatarPath = "/new/avatar.png";
        String newPassword = "newlongpassword";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, newUsername, newAvatarPath, newPassword
        );

        when(mockDataAccess.isUsernameTaken(newUsername)).thenReturn(false);

        interactor.execute(inputData);

        assertEquals(newUsername, existingUser.getUsername());
        assertEquals(newAvatarPath, existingUser.getAvatarPath());
        assertEquals(newPassword, existingUser.getPassword());

        verify(mockDataAccess, times(1)).save(existingUser);

        verify(mockPresenter, times(1)).prepareSuccessView(any(UpdateProfileOutputData.class));
        verify(mockPresenter, never()).prepareFailView(anyString());
    }

    @Test
    void execute_SuccessfulUpdateOnlyUsernameWithTrimming_ShouldSaveTrimmedValue() {
        String untrimmedUsername = "  TrimmedUser  ";
        String trimmedUsername = "TrimmedUser";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, untrimmedUsername, null, null
        );

        when(mockDataAccess.isUsernameTaken(trimmedUsername)).thenReturn(false);

        interactor.execute(inputData);

        assertEquals(trimmedUsername, existingUser.getUsername());

        verify(mockDataAccess, times(1)).isUsernameTaken(trimmedUsername);
        verify(mockDataAccess, times(1)).save(existingUser);

        verify(mockPresenter, times(1)).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_SuccessfulUpdateOnlyPasswordWithTrimming_ShouldSaveTrimmedValue() {
        String untrimmedPassword = " newpass123 ";
        String trimmedPassword = "newpass123";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, null, null, untrimmedPassword
        );

        interactor.execute(inputData);

        assertEquals(trimmedPassword, existingUser.getPassword());

        verify(mockDataAccess, times(1)).save(existingUser);

        verify(mockPresenter, times(1)).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_SuccessfulUpdateOnlyAvatarPath_ShouldSaveAndPresentSuccess() {
        String newAvatarPath = "/new/path.svg";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, null, newAvatarPath, null
        );

        interactor.execute(inputData);

        assertEquals(newAvatarPath, existingUser.getAvatarPath());
        verify(mockDataAccess, times(1)).save(existingUser);
        verify(mockPresenter, times(1)).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_SuccessfulNoUpdates_ShouldSaveOriginalDataAndPresentSuccess() {
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, null, "", " "
        );

        interactor.execute(inputData);

        assertEquals(OLD_USERNAME, existingUser.getUsername());
        assertEquals(OLD_PASSWORD, existingUser.getPassword());

        verify(mockDataAccess, times(1)).save(existingUser);

        verify(mockPresenter, times(1)).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_UsernameNotChanged_ShouldNotCheckIsUsernameTaken() {
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, OLD_USERNAME, null, null
        );

        interactor.execute(inputData);

        verify(mockDataAccess, never()).isUsernameTaken(anyString());
        verify(mockDataAccess, times(1)).save(existingUser);
        verify(mockPresenter, times(1)).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_FailUserDoesNotExist_ShouldPresentFailView() {
        String invalidUid = "non-existent-user";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                invalidUid, "NewName", null, null
        );
        when(mockDataAccess.existsByUid(invalidUid)).thenReturn(false);

        interactor.execute(inputData);

        verify(mockPresenter, times(1)).prepareFailView("User does not exist.");
        verify(mockDataAccess, never()).getByUid(anyString());
        verify(mockDataAccess, never()).save(any(User.class));
        verify(mockPresenter, never()).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_FailUsernameTooLong_ShouldPresentFailView() {
        String longUsername = "averylongname123";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, longUsername, null, null
        );

        interactor.execute(inputData);

        verify(mockPresenter, times(1)).prepareFailView("Username must be 1–12 characters.");
        verify(mockDataAccess, never()).isUsernameTaken(anyString());
        verify(mockDataAccess, never()).save(any(User.class));
        verify(mockPresenter, never()).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_FailUsernameIsEmptyAfterTrimming_ShouldPresentFailView() {
        String blankUsername = "  ";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, blankUsername, null, null
        );

        interactor.execute(inputData);

        verify(mockPresenter, times(1)).prepareFailView("Username must be 1–12 characters.");
        verify(mockDataAccess, never()).isUsernameTaken(anyString());
        verify(mockDataAccess, never()).save(any(User.class));
    }

    @Test
    void execute_FailUsernameAlreadyTaken_ShouldPresentFailView() {
        String takenUsername = "TakenName";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, takenUsername, null, null
        );

        when(mockDataAccess.isUsernameTaken(takenUsername)).thenReturn(true);

        interactor.execute(inputData);

        verify(mockPresenter, times(1)).prepareFailView("Username already taken.");
        verify(mockDataAccess, never()).save(any(User.class));
        verify(mockPresenter, never()).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_FailPasswordTooShort_ShouldPresentFailView() {

        String shortPassword = "short";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, null, null, shortPassword
        );

        interactor.execute(inputData);

        verify(mockPresenter, times(1)).prepareFailView("Password must be longer than 6 characters.");
        verify(mockDataAccess, never()).save(any(User.class));
        verify(mockPresenter, never()).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_SuccessWithOnlyUsernameChangeAndNoPasswordChange_CheckPasswordNotOverwritten() {
        // Arrange
        String newUsername = "NewName";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, newUsername, null, null
        );
        when(mockDataAccess.isUsernameTaken(newUsername)).thenReturn(false);

        interactor.execute(inputData);

        assertEquals(newUsername, existingUser.getUsername());
        assertEquals(OLD_PASSWORD, existingUser.getPassword());
        verify(mockDataAccess, times(1)).save(existingUser);
        verify(mockPresenter, times(1)).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    @Test
    void execute_SuccessWithOnlyPasswordChangeAndNoUsernameChange_CheckUsernameNotOverwritten() {
        String newPassword = "newpasswordlongenough";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                UID, null, null, newPassword
        );

        interactor.execute(inputData);

        assertEquals(OLD_USERNAME, existingUser.getUsername());
        assertEquals(newPassword, existingUser.getPassword());
        verify(mockDataAccess, times(1)).save(existingUser);
        verify(mockPresenter, times(1)).prepareSuccessView(any(UpdateProfileOutputData.class));
    }

    private static class UpdateProfileInputData {
        private final String uid;
        private final String newUsername;
        private final String newAvatarPath;
        private final String newPassword;

        public UpdateProfileInputData(String uid, String newUsername, String newAvatarPath, String newPassword) {
            this.uid = uid;
            this.newUsername = newUsername;
            this.newAvatarPath = newAvatarPath;
            this.newPassword = newPassword;
        }

        public String getUid() { return uid; }
        public String getNewUsername() { return newUsername; }
        public String getNewAvatarPath() { return newAvatarPath; }
        public String getNewPassword() { return newPassword; }
    }

    private static class UpdateProfileOutputData {
        private final String uid;
        private final String username;
        private final String avatarPath;
        private final String password;
        private final boolean isSuccess;

        public UpdateProfileOutputData(String uid, String username, String avatarPath, String password, boolean isSuccess) {
            this.uid = uid;
            this.username = username;
            this.avatarPath = avatarPath;
            this.password = password;
            this.isSuccess = isSuccess;
        }
    }
}