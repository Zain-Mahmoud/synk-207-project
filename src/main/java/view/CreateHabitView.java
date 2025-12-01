package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.create_habit.CreateHabitController;
import interface_adapter.create_habit.CreateHabitState;
import interface_adapter.create_habit.CreateHabitViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * View for creating a new Habit.
 * All business attributes are entered by the user, while the username
 * is taken from the LoggedInViewModel.
 */
public class CreateHabitView extends JPanel implements ActionListener, PropertyChangeListener {

    private final CreateHabitViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private CreateHabitController controller;

    private final JTextField habitNameField = new JTextField(15);
    private final JTextField startDateTimeField = new JTextField(15);
    private final JTextField frequencyField = new JTextField(15);
    private final JTextField habitGroupField = new JTextField(15);
    private final JTextField streakCountField = new JTextField(15);
    private final JTextField priorityField = new JTextField(15);

    private final JLabel messageLabel = new JLabel(" ");

    private final JButton createButton = new JButton("Create");
    private final JButton cancelButton = new JButton("Cancel");

    public CreateHabitView(CreateHabitViewModel viewModel,
                           ViewManagerModel viewManagerModel,
                           LoggedInViewModel loggedInViewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;

        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Create New Habit", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Habit Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(habitNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Start Date & Time (yyyy-MM-ddTHH:mm):"), gbc);
        gbc.gridx = 1;
        formPanel.add(startDateTimeField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Frequency (days):"), gbc);
        gbc.gridx = 1;
        formPanel.add(frequencyField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Habit Group:"), gbc);
        gbc.gridx = 1;
        formPanel.add(habitGroupField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Streak Count:"), gbc);
        gbc.gridx = 1;
        formPanel.add(streakCountField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Priority (integer):"), gbc);
        gbc.gridx = 1;
        formPanel.add(priorityField, gbc);
        row++;

        add(formPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel();
        buttons.add(createButton);
        buttons.add(cancelButton);
        southPanel.add(buttons, BorderLayout.NORTH);
        southPanel.add(messageLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreate();
            }
        });

        cancelButton.addActionListener(e -> {
            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        });
    }

    private void handleCreate() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Create Habit controller not initialized.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = loggedInViewModel.getState().getUsername();
        String habitName = habitNameField.getText().trim();
        String startDateTimeText = startDateTimeField.getText().trim();
        String frequencyText = frequencyField.getText().trim();
        String habitGroup = habitGroupField.getText().trim();
        String streakText = streakCountField.getText().trim();
        String priorityText = priorityField.getText().trim();

        if (habitName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Habit name cannot be empty.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int streakCount = streakText.isEmpty() ? 0 : Integer.parseInt(streakText);
            int priority = Integer.parseInt(priorityText);

            controller.execute(username, habitName, startDateTimeText,
                    frequencyText, habitGroup, streakCount, priority);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input: " + ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
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
        CreateHabitState state = viewModel.getState();
        if (state.getErrorMessage() != null) {
            messageLabel.setText(state.getErrorMessage());
        } else if (state.getSuccessMessage() != null) {
            messageLabel.setText(state.getSuccessMessage());
            habitNameField.setText("");
            startDateTimeField.setText("");
            frequencyField.setText("");
            habitGroupField.setText("");
            streakCountField.setText("");
            priorityField.setText("");

            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        } else {
            messageLabel.setText(" ");
        }
    }

    public String getViewName() {
        return viewModel.getViewName();
    }

    public void setCreateHabitController(CreateHabitController controller) {
        this.controller = controller;
    }
}
