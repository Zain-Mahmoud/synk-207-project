package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logging into the program (JavaFX).
 */
public class LoginView extends VBox implements PropertyChangeListener {

    private final String viewName = "log in";
    private final LoginViewModel loginViewModel;

    private final TextField usernameInputField = new TextField();
    private final Label usernameErrorField = new Label();
    private final PasswordField passwordInputField = new PasswordField();

    private final Button logIn;
    private final Button toSignup;
    private LoginController loginController = null;

    public LoginView(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        // ── Style classes ──
        this.getStyleClass().add("auth-container");
        this.setAlignment(Pos.CENTER);

        // ── Title ──
        Label title = new Label("SYNK");
        title.getStyleClass().add("auth-title");

        Label subtitle = new Label("Sign in to your account");
        subtitle.getStyleClass().add("auth-subtitle");

        // ── Username ──
        Label usernameLabel = new Label("Username");
        usernameLabel.getStyleClass().add("auth-label");
        usernameInputField.setPromptText("Enter your username");
        usernameInputField.getStyleClass().add("auth-field");

        usernameErrorField.getStyleClass().add("auth-error");

        // ── Password ──
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("auth-label");
        passwordInputField.setPromptText("Enter your password");
        passwordInputField.getStyleClass().add("auth-field");

        // ── Buttons ──
        logIn = new Button("Sign In");
        logIn.getStyleClass().add("auth-btn-primary");

        toSignup = new Button("Don't have an account? Sign up");
        toSignup.getStyleClass().add("auth-btn-secondary");

        // ── Events ──
        logIn.setOnAction(evt -> {
            final LoginState currentState = loginViewModel.getState();
            loginController.execute(
                    currentState.getUsername(),
                    currentState.getPassword()
            );
        });

        usernameInputField.textProperty().addListener((obs, oldVal, newVal) -> {
            final LoginState currentState = loginViewModel.getState();
            currentState.setUsername(newVal);
            loginViewModel.setState(currentState);
        });

        passwordInputField.textProperty().addListener((obs, oldVal, newVal) -> {
            final LoginState currentState = loginViewModel.getState();
            currentState.setPassword(newVal);
            loginViewModel.setState(currentState);
        });

        // ── Layout ──
        VBox formBox = new VBox(4, usernameLabel, usernameInputField, usernameErrorField,
                passwordLabel, passwordInputField);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setMaxWidth(300);

        this.getChildren().addAll(title, subtitle, formBox, logIn, toSignup);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            final LoginState state = (LoginState) evt.getNewValue();
            setFields(state);
            usernameErrorField.setText(
                    state.getLoginError() != null ? state.getLoginError() : "");
        });
    }

    private void setFields(LoginState state) {
        if (!usernameInputField.getText().equals(state.getUsername())) {
            usernameInputField.setText(state.getUsername());
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    /**
     * Provide access to toSignup button so AppBuilder / SignupView can hook navigation.
     */
    public Button getToSignupButton() {
        return toSignup;
    }
}
