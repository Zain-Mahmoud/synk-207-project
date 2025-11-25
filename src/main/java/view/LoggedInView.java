package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarController;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarControllerState;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarViewModel;
import interface_adapter.view_stats.ViewStatsController;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logged into the program.
 */
public class LoggedInView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "logged in";
    private final LoggedInViewModel loggedInViewModel;
    private final JLabel passwordErrorField = new JLabel();
    private ChangePasswordController changePasswordController = null;
    private ViewManagerModel viewManagerModel;
    private SyncToGoogleCalendarController syncToGoogleCalendarController; // Injected controller to kick off calendar sync
    private SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel; //  View model providing sync result updates
    private ViewStatsController viewStatsController;
    private final JLabel username;

    private final JButton logOut;
    private final JButton viewLeaderboard;
    private final JButton syncCalendarButton; //  Button to sync tasks to Google Calendar
    private final JLabel syncStatusLabel = new JLabel(); //  Inline status label for sync results

    private final JButton viewStats;

    private final JTextField passwordInputField = new JTextField(15);
    private final JButton changePassword;

    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("Logged In Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel passwordInfo = new LabelTextPanel(
                new JLabel("Password"), passwordInputField);

        final JLabel usernameInfo = new JLabel("Currently logged in: ");
        username = new JLabel();

        final JPanel buttons = new JPanel();
        logOut = new JButton("Log Out");
        buttons.add(logOut);

        changePassword = new JButton("Change Password");
        buttons.add(changePassword);

        viewLeaderboard = new JButton("View Leaderboard");
        buttons.add(viewLeaderboard);

        syncCalendarButton = new JButton("Sync to Google Calendar"); //  Create sync trigger button
        buttons.add(syncCalendarButton); // Add sync button alongside other actions

        viewStats = new JButton("View statistics");
        buttons.add(viewStats);

        logOut.addActionListener(this);
        
        viewLeaderboard.addActionListener(evt -> {
            if (evt.getSource().equals(viewLeaderboard) && viewManagerModel != null) {
                viewManagerModel.setState("leaderboard");
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

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoggedInState currentState = loggedInViewModel.getState();
                currentState.setPassword(passwordInputField.getText());
                loggedInViewModel.setState(currentState);
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

        changePassword.addActionListener(
                // This creates an anonymous subclass of ActionListener and instantiates it.
                evt -> {
                    if (evt.getSource().equals(changePassword)) {
                        final LoggedInState currentState = loggedInViewModel.getState();

                        this.changePasswordController.execute(
                                currentState.getUsername(),
                                currentState.getPassword()
                        );
                    }
                }
        );

        syncCalendarButton.addActionListener(evt -> { // Invoke calendar sync when button is clicked
            if (evt.getSource().equals(syncCalendarButton) && syncToGoogleCalendarController != null) {
                final LoggedInState currentState = loggedInViewModel.getState(); // Use logged-in username as user identifier for sync
                syncToGoogleCalendarController.execute(currentState.getUsername()); // Trigger sync interactor via controller
            }
        });

        this.add(title);
        this.add(usernameInfo);
        this.add(username);

        this.add(passwordInfo);
        this.add(passwordErrorField);
        this.add(buttons);
        this.add(syncStatusLabel); // Show latest sync success/error message in UI
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
            username.setText(state.getUsername());
        }
        else if (evt.getPropertyName().equals("password")) {
            final LoggedInState state = (LoggedInState) evt.getNewValue();
            JOptionPane.showMessageDialog(null, "password updated for " + state.getUsername());
        }
        else if (evt.getPropertyName().equals("sync")) { // React to sync view model updates
            final SyncToGoogleCalendarControllerState syncState = (SyncToGoogleCalendarControllerState) evt.getNewValue(); //  Capture sync state updates
            if (syncState.isSuccess()) {
                syncStatusLabel.setText(syncState.getStatusMessage()); // Reflect successful sync in label
                JOptionPane.showMessageDialog(this, syncState.getStatusMessage(), "Sync Complete", JOptionPane.INFORMATION_MESSAGE); //  Show confirmation dialog on success
            } else {
                syncStatusLabel.setText(syncState.getError()); //  Show error on failure
                JOptionPane.showMessageDialog(this, syncState.getError(), "Sync Failed", JOptionPane.ERROR_MESSAGE); //  Alert user when sync fails
            }
        }

    }

    public String getViewName() {
        return viewName;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public void setSyncToGoogleCalendarController(SyncToGoogleCalendarController syncToGoogleCalendarController) { //  Allow builder to inject sync controller
        this.syncToGoogleCalendarController = syncToGoogleCalendarController; //  Store injected sync controller
    }

    public void setSyncToGoogleCalendarViewModel(SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel) { //  Subscribe to sync view model updates
        this.syncToGoogleCalendarViewModel = syncToGoogleCalendarViewModel; //  Capture sync view model reference
        this.syncToGoogleCalendarViewModel.addPropertyChangeListener(this); // Listen for sync result changes
    }

    public void setViewStatsController(ViewStatsController viewStatsController){
        this.viewStatsController = viewStatsController;
    }
}
