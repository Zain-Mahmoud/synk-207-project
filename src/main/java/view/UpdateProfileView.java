package view;

import interface_adapter.update_profile.UpdateProfileController;
import interface_adapter.update_profile.UpdateProfileState;
import interface_adapter.update_profile.UpdateProfileViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for updating the user's profile (e.g., username).
 */
public class UpdateProfileView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "update profile";
    private final UpdateProfileViewModel updateProfileViewModel;
    private UpdateProfileController updateProfileController = null;

    private String currentUid;

    private final JTextField usernameInputField = new JTextField(15);
    private final JLabel usernameErrorField = new JLabel();
    private final JLabel successMessageField = new JLabel();

    private final JButton saveButton;
    private final JButton cancelButton;

    public UpdateProfileView(UpdateProfileViewModel updateProfileViewModel) {
        this.updateProfileViewModel = updateProfileViewModel;
        this.updateProfileViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("Update Profile");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel usernameInfo = new LabelTextPanel(
                new JLabel("New Username"), usernameInputField);

        final JPanel buttons = new JPanel();
        saveButton = new JButton("Save");
        buttons.add(saveButton);
        cancelButton = new JButton("Cancel");
        buttons.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(saveButton)) {
                    final UpdateProfileState currentState = updateProfileViewModel.getState();

                    updateProfileController.execute(
                            currentUid,
                            currentState.getUsername(),
                            null
                    );
                }
            }
        });

        cancelButton.addActionListener(this);

        usernameInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final UpdateProfileState currentState = updateProfileViewModel.getState();
                currentState.setUsername(usernameInputField.getText());
                updateProfileViewModel.setState(currentState);
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

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(usernameInfo);
        this.add(usernameErrorField);
        this.add(successMessageField);
        this.add(buttons);
    }

    /**
     * React to a button click that results in evt.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final UpdateProfileState state = (UpdateProfileState) evt.getNewValue();
        setFields(state);
        usernameErrorField.setText(state.getUsernameError());
        successMessageField.setText(state.getSuccessMessage());
    }

    private void setFields(UpdateProfileState state) {
        usernameInputField.setText(state.getUsername());
    }

    public String getViewName() {
        return viewName;
    }

    public void setUpdateProfileController(UpdateProfileController updateProfileController) {
        this.updateProfileController = updateProfileController;
    }

    public void setCurrentUid(String currentUid) {
        this.currentUid = currentUid;
    }
}
