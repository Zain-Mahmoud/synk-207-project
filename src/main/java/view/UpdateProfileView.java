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

    private final String viewName = "updateprofile";
    private final UpdateProfileViewModel updateProfileViewModel;
    private UpdateProfileController updateProfileController = null;

    private final JTextField usernameInputField = new JTextField(15);
    private final JLabel usernameErrorField = new JLabel();
    private final JLabel successMessageField = new JLabel();
    private final JButton chooseAvatarButton;
    private final JLabel avatarPreviewLabel = new JLabel();

    private final JButton saveButton;
    private final JButton cancelButton;

    public UpdateProfileView(UpdateProfileViewModel updateProfileViewModel) {
        this.updateProfileViewModel = updateProfileViewModel;
        this.updateProfileViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel(UpdateProfileViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel usernameInfo = new LabelTextPanel(
                new JLabel(UpdateProfileViewModel.USERNAME_LABEL), usernameInputField);

        final JPanel buttons = new JPanel();
        chooseAvatarButton = new JButton(UpdateProfileViewModel.AVATAR_LABEL);
        buttons.add(chooseAvatarButton);
        saveButton = new JButton(UpdateProfileViewModel.SAVE_BUTTON_LABEL);
        buttons.add(saveButton);
        cancelButton = new JButton(UpdateProfileViewModel.CANCEL_BUTTON_LABEL);
        buttons.add(cancelButton);

        chooseAvatarButton.addActionListener(evt -> {
            if (evt.getSource().equals(chooseAvatarButton)) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    UpdateProfileState state = updateProfileViewModel.getState();
                    state.setAvatarPath(path);
                    updateProfileViewModel.setState(state);
                    updateProfileViewModel.firePropertyChanged();
                }
            }
        });

        saveButton.addActionListener(evt -> {
            if (evt.getSource().equals(saveButton)) {
                final UpdateProfileState currentState = updateProfileViewModel.getState();

                updateProfileController.execute(
                        currentState.getUid(),
                        currentState.getUsername(),
                        currentState.getAvatarPath()
                );
            }
        });

//        cancelButton.addActionListener(evt -> {
//
//            if (evt.getSource().equals(cancelButton) && viewManagerModel != null) {
//                viewManagerModel.setState("loggedin");
//                viewManagerModel.firePropertyChanged();
//            }
//        });

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
        this.add(chooseAvatarButton);
        this.add(avatarPreviewLabel);
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
        updateAvatarPreview(state.getAvatarPath());
    }

    private void updateAvatarPreview(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            avatarPreviewLabel.setIcon(null);
            return;
        }
        ImageIcon icon = new ImageIcon(avatarPath);
        Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        avatarPreviewLabel.setIcon(new ImageIcon(img));
    }

    public String getViewName() {
        return viewName;
    }

    public void setUpdateProfileController(UpdateProfileController updateProfileController) {
        this.updateProfileController = updateProfileController;
    }

//    public void setCurrentUid(String currentUid) {
//        this.currentUid = currentUid;
//    }
}
