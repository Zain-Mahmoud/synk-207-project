package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID; // New import for unique temporary filenames

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import interface_adapter.ViewManagerModel;
import interface_adapter.update_profile.UpdateProfileController;
import interface_adapter.update_profile.UpdateProfileState;
import interface_adapter.update_profile.UpdateProfileViewModel;

@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:SuppressWarnings", "checkstyle:AnonInnerLength", "checkstyle:JavaNCSS"})
public class UpdateProfileView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "updateprofile";
    private final UpdateProfileViewModel updateProfileViewModel;
    private UpdateProfileController updateProfileController;
    private ViewManagerModel viewManagerModel;

    private final JTextField usernameInputField = new JTextField(20);
    private final JPasswordField passwordInputField = new JPasswordField(20);
    private final JLabel usernameErrorField = new JLabel(" ");
    private final JLabel passwordErrorField = new JLabel(" ");
    private final JLabel successMessageField = new JLabel(" ");
    private final JButton chooseAvatarButton;
    private final JLabel avatarPreviewLabel = new JLabel();

    private final JButton saveButton;
    private final JButton cancelButton;

    private String currentUid;

    private class FieldDocumentListener implements DocumentListener {
        private final boolean isUsername;

        public FieldDocumentListener(boolean isUsername) {
            this.isUsername = isUsername;
        }

        private void updateState() {
            final UpdateProfileState currentState = updateProfileViewModel.getState();
            if (isUsername) {
                currentState.setUsername(usernameInputField.getText());
            } else {
                currentState.setPassword(String.valueOf(passwordInputField.getPassword()));
            }
            updateProfileViewModel.setState(currentState);
        }

        @Override
        public void insertUpdate(DocumentEvent e) { updateState(); }

        @Override
        public void removeUpdate(DocumentEvent e) { updateState(); }

        @Override
        public void changedUpdate(DocumentEvent e) { updateState(); }
    }

    public UpdateProfileView(UpdateProfileViewModel updateProfileViewModel) {
        this.updateProfileViewModel = updateProfileViewModel;
        this.updateProfileViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel(UpdateProfileViewModel.TITLE_LABEL);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(50, 50, 100));

        final LabelTextPanel usernameInfo = new LabelTextPanel(
                new JLabel(UpdateProfileViewModel.USERNAME_LABEL), usernameInputField);

        final LabelTextPanel passwordInfo = new LabelTextPanel(
                new JLabel(UpdateProfileViewModel.PASSWORD_LABEL), passwordInputField);

        usernameErrorField.setForeground(new Color(200, 0, 0));
        passwordErrorField.setForeground(new Color(200, 0, 0));
        successMessageField.setForeground(new Color(0, 150, 50));

        chooseAvatarButton = new JButton(UpdateProfileViewModel.AVATAR_LABEL);
        saveButton = new JButton(UpdateProfileViewModel.SAVE_BUTTON_LABEL);
        cancelButton = new JButton(UpdateProfileViewModel.CANCEL_BUTTON_LABEL);

        Color primaryColor = new Color(75, 100, 170);
        Color secondaryColor = new Color(200, 200, 200);

        saveButton.setBackground(primaryColor);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        cancelButton.setBackground(secondaryColor);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(secondaryColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        chooseAvatarButton.setBackground(new Color(150, 150, 220));
        chooseAvatarButton.setForeground(Color.BLACK);
        chooseAvatarButton.setFocusPainted(false);
        chooseAvatarButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        avatarPreviewLabel.setPreferredSize(new Dimension(54, 54));
        avatarPreviewLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(1, 1, 1, 1),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2)
        ));

        chooseAvatarButton.addActionListener(evt -> {
            if (evt.getSource().equals(chooseAvatarButton)) {
                chooseAvatarButtonFunction();
            }
        });

        saveButton.addActionListener(evt -> {
            if (evt.getSource().equals(saveButton)) {
                saveButtonFunction();
            }
        });

        cancelButton.addActionListener(evt -> {
            if (evt.getSource().equals(cancelButton) && viewManagerModel != null) {
                updateProfileViewModel.setState(new UpdateProfileState());
                updateProfileViewModel.firePropertyChanged();
                viewManagerModel.setState("logged in");
                viewManagerModel.firePropertyChanged();
            }
        });

        usernameInputField.getDocument().addDocumentListener(new FieldDocumentListener(true));
        passwordInputField.getDocument().addDocumentListener(new FieldDocumentListener(false));

        JPanel contentPanel = new JPanel(new GridBagLayout());

        Border line = BorderFactory.createLineBorder(new Color(180, 180, 220), 2, true);
        Border titled = BorderFactory.createTitledBorder(line, "User Profile Settings");
        Border finalBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createCompoundBorder(
                        titled,
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        );
        contentPanel.setBorder(finalBorder);
        contentPanel.setBackground(new Color(245, 245, 250));

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);

        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(title, gbc);

        gbc.gridy = 1;
        contentPanel.add(successMessageField, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(chooseAvatarButton, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(avatarPreviewLabel, gbc);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 3;
        contentPanel.add(usernameInfo, gbc);

        gbc.gridy = 4;
        contentPanel.add(usernameErrorField, gbc);

        gbc.gridy = 5;
        contentPanel.add(passwordInfo, gbc);

        gbc.gridy = 6;
        contentPanel.add(passwordErrorField, gbc);

        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        buttons.setBackground(contentPanel.getBackground());
        buttons.add(saveButton);
        buttons.add(cancelButton);

        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(buttons, gbc);

        this.setLayout(new GridBagLayout());
        this.add(contentPanel, new GridBagConstraints());
        this.setBackground(Color.WHITE);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final UpdateProfileState state = (UpdateProfileState) evt.getNewValue();
        setFields(state);
        usernameErrorField.setText(Objects.requireNonNullElse(state.getUsernameError(), " "));
        passwordErrorField.setText(Objects.requireNonNullElse(state.getPasswordError(), " "));
        successMessageField.setText(Objects.requireNonNullElse(state.getSuccessMessage(), " "));

        saveButton.setEnabled(state.getUsernameError() == null && state.getPasswordError() == null);
    }

    private void setFields(UpdateProfileState state) {
        usernameInputField.setText(state.getUsername());
        passwordInputField.setText(state.getPassword());
        updateAvatarPreview(state.getAvatarPath());
    }

    private void updateAvatarPreview(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            avatarPreviewLabel.setIcon(null);
            avatarPreviewLabel.setText("No Avatar");
            return;
        }

        avatarPreviewLabel.setText(null);
        try {
            final ImageIcon icon = new ImageIcon(avatarPath);
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                final Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                avatarPreviewLabel.setIcon(new ImageIcon(img));
            } else {
                avatarPreviewLabel.setText("Image Load Failed");
                avatarPreviewLabel.setIcon(null);
            }
        } catch (Exception e) {
            avatarPreviewLabel.setText("Preview Error");
            avatarPreviewLabel.setIcon(null);
        }
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

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void chooseAvatarButtonFunction() {
        final JFileChooser fileChooser = new JFileChooser();
        final int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileChooser.getSelectedFile();

            final File avatarsDir = new File("avatars");
            if (!avatarsDir.exists()) {
                avatarsDir.mkdirs();
            }

            final String name = selectedFile.getName();
            String extension = "";
            final int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) {
                extension = name.substring(dotIndex);
            }

            final String fallbackUid = UUID.randomUUID().toString();
            final String uidForFileName = Objects.requireNonNullElse(currentUid, fallbackUid);

            final String fileName = uidForFileName + extension;
            final File destinationFile = new File(avatarsDir, fileName);
            String newPath = null;

            try {
                Files.copy(
                        selectedFile.toPath(),
                        destinationFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
                newPath = destinationFile.getPath();
            } catch (IOException excp) {
                excp.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save avatar: " + excp.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }

            if (newPath != null) {
                final UpdateProfileState state = updateProfileViewModel.getState();
                state.setAvatarPath(newPath);
                updateProfileViewModel.setState(state);
                updateProfileViewModel.firePropertyChanged();
            }
        }
    }

    public void saveButtonFunction() {
        final UpdateProfileState currentState = updateProfileViewModel.getState();

        updateProfileController.execute(
                currentState.getUid(),
                currentState.getUsername(),
                currentState.getAvatarPath(),
                currentState.getPassword()
        );
    }
}