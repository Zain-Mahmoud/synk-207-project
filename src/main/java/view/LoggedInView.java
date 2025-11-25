package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.update_profile.ChangePasswordController;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarController;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarControllerState;
import interface_adapter.sync_to_google_calendar.SyncToGoogleCalendarViewModel;

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
    private ViewManagerModel viewManagerModel;
    private SyncToGoogleCalendarController syncToGoogleCalendarController; // Injected controller to kick off calendar sync
    private SyncToGoogleCalendarViewModel syncToGoogleCalendarViewModel; //  View model providing sync result updates

    private final JLabel username;
    private final JButton logOut;
    private final JButton viewLeaderboard;
    private final JButton updateProfile;



    public LoggedInView(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
        this.loggedInViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("Logged In Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JLabel usernameInfo = new JLabel("Currently logged in: ");
        username = new JLabel();

        final JPanel buttons = new JPanel();
        logOut = new JButton("Log Out");
        buttons.add(logOut);

        viewLeaderboard = new JButton("View Leaderboard");
        buttons.add(viewLeaderboard);

        updateProfile = new JButton("Update Profile");
        buttons.add(updateProfile);

        logOut.addActionListener(this);
        
        viewLeaderboard.addActionListener(evt -> {
            if (evt.getSource().equals(viewLeaderboard) && viewManagerModel != null) {
                viewManagerModel.setState("leaderboard");
                viewManagerModel.firePropertyChanged();
            }

        });

        updateProfile.addActionListener(evt -> {
            if (evt.getSource().equals(updateProfile) && viewManagerModel != null) {
                viewManagerModel.setState("updateprofile");
                viewManagerModel.firePropertyChanged();
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));





        syncCalendarButton.addActionListener(evt -> { // Invoke calendar sync when button is clicked
            if (evt.getSource().equals(syncCalendarButton) && syncToGoogleCalendarController != null) {
                final LoggedInState currentState = loggedInViewModel.getState(); // Use logged-in username as user identifier for sync
                syncToGoogleCalendarController.execute(currentState.getUsername()); // Trigger sync interactor via controller
            }
        });

        this.add(title);
        this.add(usernameInfo);
        this.add(username);


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
}
