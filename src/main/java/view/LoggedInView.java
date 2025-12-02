package view;

import interface_adapter.ViewManagerModel;
import view.UpdateProfileView;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarController;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarControllerState;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_stats.ViewStatsController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logged into the program, styled as a landing page.
 */
public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private UpdateProfileView updateProfileView;

    private ViewManagerModel viewManagerModel;
    private SyncToGoogleCalendarController syncToGoogleCalendarController;
    private ViewTasksAndHabitsController viewTasksAndHabitsController;
    private SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel;
    private ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel;

    private ViewStatsController viewStatsController;
    private final JLabel username;
    private final JLabel avatarLabel;

    private final JButton logOut;
    private final JButton viewTasksAndHabits;
    private final JButton viewLeaderboard;
    private final JButton updateProfile;
    private final JButton syncCalendarButton;
    private final JLabel syncStatusLabel = new JLabel();

    private final JButton viewStats;

    // --- Styling Constants ---
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246); // Tailwind blue-500
    private static final Color SECONDARY_COLOR = new Color(243, 244, 246); // Tailwind gray-100
    private static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 64);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font USER_INFO_FONT = new Font("SansSerif", Font.PLAIN, 16);

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        this.setBackground(SECONDARY_COLOR);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50, 50, 50, 50));

        final JLabel title = new JLabel("SYNK");
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        username = new JLabel();
        username.setFont(USER_INFO_FONT);
        final JLabel usernameInfo = new JLabel("Welcome back, ");
        usernameInfo.setFont(USER_INFO_FONT);

        avatarLabel = new JLabel();
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); // Padding next to avatar

        JPanel userPanel = new JPanel();
        userPanel.setBackground(SECONDARY_COLOR);
        userPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Center everything in the flow
        userPanel.add(avatarLabel);
        userPanel.add(usernameInfo);
        userPanel.add(username);
        userPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JPanel buttons = new JPanel();
        buttons.setBackground(SECONDARY_COLOR);
        buttons.setLayout(new GridLayout(3, 2, 15, 15)); // 3 rows, 2 columns, with 15px gap
        buttons.setBorder(new EmptyBorder(25, 100, 25, 100)); // Padding around the buttons
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        logOut = createStyledButton("Log Out");
        viewTasksAndHabits = createStyledButton("View Tasks and Habits");
        viewLeaderboard = createStyledButton("View Leaderboard");
        updateProfile = createStyledButton("Update Profile");
        syncCalendarButton = createStyledButton("Sync to Google Calendar");
        viewStats = createStyledButton("View Statistics");

        buttons.add(viewTasksAndHabits);
        buttons.add(viewStats);
        buttons.add(viewLeaderboard);
        buttons.add(updateProfile);
        buttons.add(syncCalendarButton);
        buttons.add(logOut);

        syncStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        syncStatusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        syncStatusLabel.setForeground(Color.GRAY);

        this.add(Box.createVerticalGlue()); // Push content to the center vertically
        this.add(title);
        this.add(Box.createVerticalStrut(20));
        this.add(userPanel);
        this.add(Box.createVerticalStrut(40));
        this.add(buttons);
        this.add(Box.createVerticalStrut(15));
        this.add(syncStatusLabel);
        this.add(Box.createVerticalGlue()); // Push content to the center vertically

        logOut.addActionListener(this);

        viewLeaderboard.addActionListener(evt -> {
            if (evt.getSource().equals(viewLeaderboard) && viewManagerModel != null) {
                viewManagerModel.setState("leaderboard");
                viewManagerModel.firePropertyChanged();
            }
        });

        updateProfile.addActionListener(evt -> {
            if (evt.getSource().equals(updateProfile) && viewManagerModel != null) {
                LoggedInState state = loggedInViewModel.getState();
                String uid = state.getUid();

                if (updateProfileView != null) {
                    updateProfileView.setCurrentUid(uid);
                }

                viewManagerModel.setState("updateprofile");
                viewManagerModel.firePropertyChanged();
            }
        });

        viewTasksAndHabits.addActionListener(evt -> {
            if (evt.getSource().equals(viewTasksAndHabits) && viewManagerModel != null) {
                viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);
                viewManagerModel.setState("view tasks and habits");
                viewManagerModel.firePropertyChanged();
            }
        });

        viewStats.addActionListener(evt -> {
            if (evt.getSource().equals(viewStats) && viewManagerModel != null){
                viewStatsController.execute();
                viewManagerModel.setState("view stats");
                viewManagerModel.firePropertyChanged();
            }
        });
        syncCalendarButton.addActionListener(evt -> {
            if (evt.getSource().equals(syncCalendarButton) && syncToGoogleCalendarController != null) {
                final LoggedInState currentState = loggedInViewModel.getState();
                syncToGoogleCalendarController.execute(currentState.getUsername());
            }
        });
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        return button;
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
        if (evt.getPropertyName().equals("state")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            username.setText(state.getUsername() + "!");
            updateAvatar(state.getAvatarPath());

            if (viewTasksAndHabitsController != null && state.getUsername() != null && !state.getUsername().isEmpty()) {
                this.viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);
            }
        }
        else if (evt.getPropertyName().equals("sync")) {
            final SyncToGoogleCalendarControllerState syncState = (SyncToGoogleCalendarControllerState) evt.getNewValue();
            if (syncState.isSuccess()) {
                syncStatusLabel.setText(syncState.getStatusMessage());
                syncStatusLabel.setForeground(new Color(34, 197, 94));
                JOptionPane.showMessageDialog(this, syncState.getStatusMessage(), "Sync Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                syncStatusLabel.setText("Error: " + syncState.getError());
                syncStatusLabel.setForeground(new Color(239, 68, 68));
                JOptionPane.showMessageDialog(this, syncState.getError(), "Sync Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setUpdateProfileView(UpdateProfileView updateProfileView) {
        this.updateProfileView = updateProfileView;
    }

    private void updateAvatar(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank()) {
            avatarLabel.setIcon(null);
            return;
        }
        ImageIcon icon = new ImageIcon(avatarPath);
        Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        avatarLabel.setIcon(new ImageIcon(img));
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void setSyncToGoogleCalendarController(SyncToGoogleCalendarController syncToGoogleCalendarController) {
        this.syncToGoogleCalendarController = syncToGoogleCalendarController;
    }

    public void setSyncToGoogleCalendarViewModel(SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel) {
        this.syncToGoogleCalendarViewModel = syncToGoogleCalendarViewModel;
        this.syncToGoogleCalendarViewModel.addPropertyChangeListener(this);
    }

    public void setViewTasksAndHabitsController(ViewTasksAndHabitsController viewTasksAndHabitsController) {
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
    }

    public void setViewStatsController(ViewStatsController viewStatsController){
        this.viewStatsController = viewStatsController;
    }
}