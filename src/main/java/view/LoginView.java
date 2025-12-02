package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logging into the program.
 */
public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final String viewName = "log in";
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font ERROR_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Color PRIMARY_COLOR = new Color(88, 101, 242);
    private static final Color SECONDARY_COLOR = new Color(242, 244, 248);
    private static final Color CARD_BORDER_COLOR = new Color(218, 222, 232);
    private static final Color MUTED_TEXT_COLOR = new Color(46, 46, 46);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    private final LoginViewModel loginViewModel;

    private final JTextField usernameInputField = new JTextField(15);
    private final JLabel usernameErrorField = new JLabel(" ");

    private final JPasswordField passwordInputField = new JPasswordField(15);
    private final JLabel passwordErrorField = new JLabel(" ");

    private final JButton logIn;
    private final JButton cancel;
    private LoginController loginController = null;

    public LoginView(LoginViewModel loginViewModel) {

        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        setLayout(new GridBagLayout());
        setBackground(SECONDARY_COLOR);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        final JLabel title = new JLabel("Login");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR.darker());

        final JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(LABEL_FONT);
        usernameLabel.setForeground(MUTED_TEXT_COLOR);
        final LabelTextPanel usernameInfo = new LabelTextPanel(usernameLabel, usernameInputField);

        final JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(LABEL_FONT);
        passwordLabel.setForeground(MUTED_TEXT_COLOR);
        final LabelTextPanel passwordInfo = new LabelTextPanel(passwordLabel, passwordInputField);

        usernameErrorField.setFont(ERROR_FONT);
        usernameErrorField.setForeground(ERROR_COLOR);
        passwordErrorField.setFont(ERROR_FONT);
        passwordErrorField.setForeground(ERROR_COLOR);

        final JPanel buttons = new JPanel();
        buttons.setBackground(Color.WHITE);
        logIn = new JButton("Log in");
        cancel = new JButton("Cancel");

        stylePrimaryButton(logIn);
        styleSecondaryButton(cancel);

        buttons.add(logIn);
        buttons.add(cancel);

        logIn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(logIn)) {
                            final LoginState currentState = loginViewModel.getState();

                            loginController.execute(
                                    currentState.getUsername(),
                                    currentState.getPassword()
                            );
                        }
                    }
                }
        );

        cancel.addActionListener(this);

        usernameInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setUsername(usernameInputField.getText());
                loginViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setPassword(new String(passwordInputField.getPassword()));
                loginViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameErrorField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordErrorField.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(24));
        card.add(usernameInfo);
        card.add(usernameErrorField);
        card.add(Box.createVerticalStrut(10));
        card.add(passwordInfo);
        card.add(passwordErrorField);
        card.add(Box.createVerticalStrut(20));
        card.add(buttons);

        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.gridx = 0;
        rootGbc.gridy = 0;
        rootGbc.weightx = 1.0;
        rootGbc.weighty = 1.0;
        rootGbc.fill = GridBagConstraints.NONE;
        rootGbc.anchor = GridBagConstraints.CENTER;

        this.add(card, rootGbc);
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
    }

    /**
     * React to a button click that results in evt.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LoginState state = (LoginState) evt.getNewValue();
        setFields(state);
        usernameErrorField.setText(state.getLoginError());
    }

    private void setFields(LoginState state) {
        usernameInputField.setText(state.getUsername());
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
