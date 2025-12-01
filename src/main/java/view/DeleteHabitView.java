package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.delete_habit.DeleteHabitController;
import interface_adapter.delete_habit.DeleteHabitState;
import interface_adapter.delete_habit.DeleteHabitViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View for deleting a habit.
 * User only needs to enter the habit name; username is taken from
 * the LoggedInViewModel.
 */
public class DeleteHabitView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "delete habit";

    private final DeleteHabitViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private DeleteHabitController deleteHabitController;

    private final JTextField habitNameField = new JTextField(15);
    private final JButton deleteButton = new JButton("Delete");
    private final JButton cancelButton = new JButton("Cancel");

    public DeleteHabitView(DeleteHabitViewModel viewModel,
                           ViewManagerModel viewManagerModel,
                           LoggedInViewModel loggedInViewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;

        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Delete Habit", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        center.add(new JLabel("Habit Name:"), gbc);
        gbc.gridx = 1;
        center.add(habitNameField, gbc);

        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(deleteButton);
        buttons.add(cancelButton);
        add(buttons, BorderLayout.SOUTH);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });

        cancelButton.addActionListener(e -> {
            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        });
    }

    private void handleDelete() {
        if (deleteHabitController == null) {
            JOptionPane.showMessageDialog(this,
                    "Delete Habit controller not initialized.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = loggedInViewModel.getState().getUsername();
        String habitName = habitNameField.getText().trim();

        if (habitName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Habit name cannot be empty.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DeleteHabitState state = viewModel.getState();
        state.setUsername(username);
        state.setHabitName(habitName);
        viewModel.setState(state);

        deleteHabitController.execute(username, habitName);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // unused
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) {
            return;
        }
        DeleteHabitState state = viewModel.getState();
        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (state.getSuccessMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getSuccessMessage(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            habitNameField.setText("");

            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setDeleteHabitController(DeleteHabitController controller) {
        this.deleteHabitController = controller;
    }
}
