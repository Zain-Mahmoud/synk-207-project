package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.update_profile.UpdateProfileController;
import interface_adapter.update_profile.UpdateProfileState;
import interface_adapter.update_profile.UpdateProfileViewModel;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

public class UpdateProfileView extends VBox implements PropertyChangeListener {

    private final String viewName = "updateprofile";
    private final UpdateProfileViewModel updateProfileViewModel;
    private UpdateProfileController updateProfileController;
    private ViewManagerModel viewManagerModel;

    private final TextField usernameInputField = new TextField();
    private final PasswordField passwordInputField = new PasswordField();
    private final Label usernameErrorField = new Label();
    private final Label passwordErrorField = new Label();
    private final Label successMessageField = new Label();
    private final ImageView avatarPreview = new ImageView();

    private final Button chooseAvatarButton;
    private final Button saveButton;
    private final Button cancelButton;

    private String currentUid;

    public UpdateProfileView(UpdateProfileViewModel updateProfileViewModel) {
        this.updateProfileViewModel = updateProfileViewModel;
        this.updateProfileViewModel.addPropertyChangeListener(this);

        this.getStyleClass().add("form-container");
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(30, 50, 30, 50));
        this.setSpacing(12);

        Label title = new Label(UpdateProfileViewModel.TITLE_LABEL);
        title.getStyleClass().add("form-title");

        usernameInputField.getStyleClass().add("form-field");
        passwordInputField.getStyleClass().add("form-field");

        usernameErrorField.getStyleClass().add("auth-error");
        passwordErrorField.getStyleClass().add("auth-error");
        successMessageField.setStyle("-fx-text-fill: #22C55E; -fx-font-size: 13px;");

        avatarPreview.setFitWidth(48);
        avatarPreview.setFitHeight(48);
        avatarPreview.setPreserveRatio(true);

        chooseAvatarButton = new Button(UpdateProfileViewModel.AVATAR_LABEL);
        chooseAvatarButton.getStyleClass().add("dashboard-btn");
        chooseAvatarButton.setPrefWidth(160);

        saveButton = new Button(UpdateProfileViewModel.SAVE_BUTTON_LABEL);
        saveButton.getStyleClass().add("form-btn-save");

        cancelButton = new Button(UpdateProfileViewModel.CANCEL_BUTTON_LABEL);
        cancelButton.getStyleClass().add("form-btn-cancel");

        // Events
        usernameInputField.textProperty().addListener((obs, ov, nv) -> {
            UpdateProfileState state = updateProfileViewModel.getState();
            state.setUsername(nv);
            updateProfileViewModel.setState(state);
        });

        passwordInputField.textProperty().addListener((obs, ov, nv) -> {
            UpdateProfileState state = updateProfileViewModel.getState();
            state.setPassword(nv);
            updateProfileViewModel.setState(state);
        });

        chooseAvatarButton.setOnAction(evt -> chooseAvatarButtonFunction());

        saveButton.setOnAction(evt -> saveButtonFunction());

        cancelButton.setOnAction(evt -> {
            if (viewManagerModel != null) {
                updateProfileViewModel.setState(new UpdateProfileState());
                updateProfileViewModel.firePropertyChanged();
                viewManagerModel.setState("logged in");
                viewManagerModel.firePropertyChanged();
            }
        });

        HBox avatarRow = new HBox(12, chooseAvatarButton, avatarPreview);
        avatarRow.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(12, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        this.getChildren().addAll(title,
                successMessageField,
                avatarRow,
                createField(UpdateProfileViewModel.USERNAME_LABEL, usernameInputField),
                usernameErrorField,
                createField(UpdateProfileViewModel.PASSWORD_LABEL, passwordInputField),
                passwordErrorField,
                buttons);
    }

    private VBox createField(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.getStyleClass().add("form-field-label");
        VBox box = new VBox(4, label, field);
        box.setMaxWidth(400);
        return box;
    }

    private VBox createField(String labelText, PasswordField field) {
        Label label = new Label(labelText);
        label.getStyleClass().add("form-field-label");
        VBox box = new VBox(4, label, field);
        box.setMaxWidth(400);
        return box;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            final UpdateProfileState state = (UpdateProfileState) evt.getNewValue();
            setFields(state);
            usernameErrorField.setText(Objects.requireNonNullElse(state.getUsernameError(), ""));
            passwordErrorField.setText(Objects.requireNonNullElse(state.getPasswordError(), ""));
            successMessageField.setText(Objects.requireNonNullElse(state.getSuccessMessage(), ""));
            saveButton.setDisable(state.getUsernameError() != null || state.getPasswordError() != null);
        });
    }

    private void setFields(UpdateProfileState state) {
        if (!usernameInputField.getText().equals(state.getUsername())) {
            usernameInputField.setText(state.getUsername());
        }
        if (!passwordInputField.getText().equals(state.getPassword())) {
            passwordInputField.setText(state.getPassword());
        }
        updateAvatarPreview(state.getAvatarPath());
    }

    private void updateAvatarPreview(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            avatarPreview.setImage(null);
            return;
        }
        try {
            File file = new File(avatarPath);
            if (file.exists()) {
                avatarPreview.setImage(new Image(file.toURI().toString(), 48, 48, true, true));
            }
        } catch (Exception e) {
            avatarPreview.setImage(null);
        }
    }

    public String getViewName() { return viewName; }

    public void setUpdateProfileController(UpdateProfileController controller) {
        this.updateProfileController = controller;
    }

    public void setCurrentUid(String uid) {
        this.currentUid = uid;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void chooseAvatarButtonFunction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (selectedFile != null) {
            File avatarsDir = new File("avatars");
            if (!avatarsDir.exists()) {
                avatarsDir.mkdirs();
            }

            String name = selectedFile.getName();
            String extension = "";
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) {
                extension = name.substring(dotIndex);
            }

            String uidForFileName = Objects.requireNonNullElse(currentUid, UUID.randomUUID().toString());
            File destinationFile = new File(avatarsDir, uidForFileName + extension);

            try {
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                UpdateProfileState state = updateProfileViewModel.getState();
                state.setAvatarPath(destinationFile.getPath());
                updateProfileViewModel.setState(state);
                updateProfileViewModel.firePropertyChanged();
            } catch (IOException excp) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to save avatar: " + excp.getMessage());
                alert.showAndWait();
            }
        }
    }

    public void saveButtonFunction() {
        UpdateProfileState currentState = updateProfileViewModel.getState();
        updateProfileController.execute(
                currentState.getUid(),
                currentState.getUsername(),
                currentState.getAvatarPath(),
                currentState.getPassword()
        );
    }
}