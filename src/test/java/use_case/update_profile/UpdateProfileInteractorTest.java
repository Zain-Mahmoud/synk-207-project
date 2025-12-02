package use_case.update_profile;

import entities.User;
import org.junit.Test;
import use_case.gateways.UserGateway;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class UpdateProfileInteractorTest {

    private final InMemoryUserGateway userGateway = new InMemoryUserGateway();
    private final TestUpdateProfilePresenter presenter = new TestUpdateProfilePresenter();
    private final UpdateProfileBoundary interactor = new UpdateProfileInteractor(userGateway, presenter);

    @Test
    public void successUpdateProfileUpdatesAllFields() {
        User original = new User("uid-1", "oldName", "avatars/old.png", "oldPassword");
        userGateway.save(original);

        UpdateProfileInputData inputData = new UpdateProfileInputData(
                "uid-1",
                "newName",
                "avatars/uid-1.png",
                "newPassword"
        );

        interactor.execute(inputData);

        assertFalse(presenter.failed);
        assertNotNull(presenter.outputData);

        UpdateProfileOutputData out = presenter.outputData;
        assertEquals("uid-1", out.getUid());
        assertEquals("newName", out.getUsername());
        assertEquals("avatars/uid-1.png", out.getAvatarPath());
        assertEquals("newPassword", out.getPassword());
        assertFalse(out.isUseCaseFailed());

        User saved = userGateway.getByUid("uid-1");
        assertEquals("newName", saved.getUsername());
        assertEquals("avatars/uid-1.png", saved.getAvatarPath());
        assertEquals("newPassword", saved.getPassword());
    }

    @Test
    public void updateProfileFailsWhenUserDoesNotExist() {
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                "non-existent-uid",
                "anyName",
                "avatars/any.png",
                "anyPassword"
        );

        interactor.execute(inputData);

        assertTrue(presenter.failed);
        assertEquals("User does not exist.", presenter.errorMessage);
        assertNull(presenter.outputData);
    }

    @Test
    public void updateProfileWithNoChangesLeavesUserUnmodified() {
        User original = new User("uid-2", "sameName", "avatars/original.png", "originalPwd");
        userGateway.save(original);

        UpdateProfileInputData inputData = new UpdateProfileInputData(
                "uid-2",
                null,
                null,
                null
        );

        interactor.execute(inputData);

        assertFalse(presenter.failed);

        User saved = userGateway.getByUid("uid-2");
        assertEquals("sameName", saved.getUsername());
        assertEquals("avatars/original.png", saved.getAvatarPath());
        assertEquals("originalPwd", saved.getPassword());
    }

    @Test
    public void updateProfileWithSameUsernameSucceedsAndKeepsUsername() {
        User original = new User("uid-3", "userA", "avatars/a.png", "pwdA");
        userGateway.save(original);

        UpdateProfileInputData inputData = new UpdateProfileInputData(
                "uid-3",
                "userA",
                null,
                null
        );

        interactor.execute(inputData);

        assertFalse(presenter.failed);

        User saved = userGateway.getByUid("uid-3");
        assertEquals("userA", saved.getUsername());
        assertEquals("avatars/a.png", saved.getAvatarPath());
        assertEquals("pwdA", saved.getPassword());
    }

    @Test
    public void updateProfileFailsWhenUsernameTooLong() {
        User original = new User("uid-4", "name4", "avatars/4.png", "pwd4");
        userGateway.save(original);

        String longName = "abcdefghijklmn";
        UpdateProfileInputData inputData = new UpdateProfileInputData(
                "uid-4",
                longName,
                null,
                null
        );

        interactor.execute(inputData);

        assertTrue(presenter.failed);
        assertEquals("Username must be 1–12 characters.", presenter.errorMessage);

        User saved = userGateway.getByUid("uid-4");
        assertEquals("name4", saved.getUsername());
    }

    @Test
    public void updateProfileFailsWhenUsernameAlreadyTaken() {
        User user1 = new User("uid-5", "userA", "avatars/a.png", "pwdA");
        User user2 = new User("uid-6", "takenName", "avatars/b.png", "pwdB");
        userGateway.save(user1);
        userGateway.save(user2);

        UpdateProfileInputData inputData = new UpdateProfileInputData(
                "uid-5",
                "takenName",
                null,
                null
        );

        interactor.execute(inputData);

        assertTrue(presenter.failed);
        assertEquals("Username already taken.", presenter.errorMessage);

        User saved = userGateway.getByUid("uid-5");
        assertEquals("userA", saved.getUsername());
    }

    @Test
    public void updateProfileFailsWhenPasswordTooShort() {
        User original = new User("uid-7", "userP", "avatars/p.png", "oldPwd");
        userGateway.save(original);

        UpdateProfileInputData inputData = new UpdateProfileInputData(
                "uid-7",
                null,
                null,
                "123"
        );

        interactor.execute(inputData);

        assertTrue(presenter.failed);
        assertEquals("Password must be longer than 6 characters.", presenter.errorMessage);

        User saved = userGateway.getByUid("uid-7");
        assertEquals("oldPwd", saved.getPassword());
    }

    private static class InMemoryUserGateway implements UserGateway {

        private final Map<String, User> usersByUid = new HashMap<>();
        private final Map<String, User> usersByName = new HashMap<>();

        @Override
        public boolean existsByUid(String uid) {
            return usersByUid.containsKey(uid);
        }

        @Override
        public boolean existsByName(String identifier) {
            return false;
        }

        @Override
        public void updateUser(String oldUserID, User user) {

        }

        @Override
        public void deleteUser(String userId) {

        }

        @Override
        public User getByUid(String uid) {
            return usersByUid.get(uid);
        }

        @Override
        public User getByName(String username) {
            return null;
        }

        @Override
        public boolean isUsernameTaken(String username) {
            return usersByName.containsKey(username);
        }

        @Override
        public void save(User user) {
            usersByUid.put(user.getUid(), user);
            usersByName.put(user.getUsername(), user);
        }
    }

    private static class TestUpdateProfilePresenter implements UpdateProfileOutputBoundary {

        UpdateProfileOutputData outputData;
        boolean failed;
        String errorMessage;

        @Override
        public void prepareSuccessView(UpdateProfileOutputData response) {
            this.outputData = response;
            this.failed = false;
            this.errorMessage = null;
        }

        @Override
        public void prepareFailView(String error) {
            this.failed = true;
            this.errorMessage = error;
            this.outputData = null;
        }
    }
}
