package view;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupState;
import interface_adapter.signup.SignupViewModel;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for the Signup Use Case (JavaFX).
 */
public class SignupView extends VBox implements PropertyChangeListener {
    private final String viewName = "sign up";

    private final SignupViewModel signupViewModel;
    private final TextField usernameInputField = new TextField();
    private final PasswordField passwordInputField = new PasswordField();
    private final PasswordField repeatPasswordInputField = new PasswordField();
    private SignupController signupController = null;

    private final Button signUp;
    private final Button toLogin;

    public SignupView(SignupViewModel signupViewModel) {
        this.signupViewModel = signupViewModel;
        signupViewModel.addPropertyChangeListener(this);

        // ── Style ──
        this.getStyleClass().add("auth-container");
        this.setAlignment(Pos.CENTER);

        // ── Title ──
        Label title = new Label("SYNK");
        title.getStyleClass().add("auth-title");

        Label subtitle = new Label("Create your account");
        subtitle.getStyleClass().add("auth-subtitle");

        // ── Fields ──
        Label usernameLabel = new Label(SignupViewModel.USERNAME_LABEL);
        usernameLabel.getStyleClass().add("auth-label");
        usernameInputField.setPromptText("Choose a username");
        usernameInputField.getStyleClass().add("auth-field");

        Label passwordLabel = new Label(SignupViewModel.PASSWORD_LABEL);
        passwordLabel.getStyleClass().add("auth-label");
        passwordInputField.setPromptText("Choose a password");
        passwordInputField.getStyleClass().add("auth-field");

        Label repeatPasswordLabel = new Label(SignupViewModel.REPEAT_PASSWORD_LABEL);
        repeatPasswordLabel.getStyleClass().add("auth-label");
        repeatPasswordInputField.setPromptText("Re-enter your password");
        repeatPasswordInputField.getStyleClass().add("auth-field");

        // ── Buttons ──
        signUp = new Button(SignupViewModel.SIGNUP_BUTTON_LABEL);
        signUp.getStyleClass().add("auth-btn-primary");

        toLogin = new Button(SignupViewModel.TO_LOGIN_BUTTON_LABEL);
        toLogin.getStyleClass().add("auth-btn-secondary");

        // ── Events ──
        signUp.setOnAction(evt -> {
            final SignupState currentState = signupViewModel.getState();
            signupController.execute(
                    currentState.getUsername(),
                    currentState.getPassword(),
                    currentState.getRepeatPassword()
            );
        });

        toLogin.setOnAction(evt -> signupController.switchToLoginView());

        usernameInputField.textProperty().addListener((obs, oldVal, newVal) -> {
            final SignupState currentState = signupViewModel.getState();
            currentState.setUsername(newVal);
            signupViewModel.setState(currentState);
        });

        passwordInputField.textProperty().addListener((obs, oldVal, newVal) -> {
            final SignupState currentState = signupViewModel.getState();
            currentState.setPassword(newVal);
            signupViewModel.setState(currentState);
        });

        repeatPasswordInputField.textProperty().addListener((obs, oldVal, newVal) -> {
            final SignupState currentState = signupViewModel.getState();
            currentState.setRepeatPassword(newVal);
            signupViewModel.setState(currentState);
        });

        // ── Layout ──
        VBox formBox = new VBox(4,
                usernameLabel, usernameInputField,
                passwordLabel, passwordInputField,
                repeatPasswordLabel, repeatPasswordInputField);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setMaxWidth(300);

        this.getChildren().addAll(title, subtitle, formBox, signUp, toLogin);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            final SignupState state = (SignupState) evt.getNewValue();
            if (state.getUsernameError() != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Signup Error");
                alert.setHeaderText(null);
                alert.setContentText(state.getUsernameError());
                alert.showAndWait();
            }
        });
    }

    public String getViewName() {
        return viewName;
    }

    public void setSignupController(SignupController controller) {
        this.signupController = controller;
    }
}
