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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

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
    private final UtilDateModel startDateModel = buildDateModel();
    private final JDatePickerImpl startDatePicker = buildDatePicker(startDateModel);
    private final JSpinner startTimeSpinner = buildTimeSpinner();
    private final JSpinner frequencySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
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
        formPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(startDatePicker, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        formPanel.add(startTimeSpinner, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Frequency (days):"), gbc);
        gbc.gridx = 1;
        frequencySpinner.setEditor(new JSpinner.NumberEditor(frequencySpinner, "#"));
        formPanel.add(frequencySpinner, gbc);
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
        LocalDateTime startDateTime;
        try {
            startDateTime = buildDateTime(startDatePicker, startTimeSpinner);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int frequency = (int) frequencySpinner.getValue();
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

            controller.execute(username, habitName, startDateTime,
                    frequency, habitGroup, streakCount, priority);
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
            resetDateModel(startDateModel);
            startTimeSpinner.setValue(new Date());
            frequencySpinner.setValue(1);
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

    private static UtilDateModel buildDateModel() {
        UtilDateModel model = new UtilDateModel();
        resetDateModel(model);
        return model;
    }

    private static void resetDateModel(UtilDateModel model) {
        Calendar now = Calendar.getInstance();
        model.setDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        model.setSelected(true);
    }

    private static JDatePickerImpl buildDatePicker(UtilDateModel model) {
        Properties props = new Properties();
        props.put("text.today", "Today");
        props.put("text.month", "Month");
        props.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, props);
        return new JDatePickerImpl(datePanel, new JFormattedTextField.AbstractFormatter() {
            private final String pattern = "yyyy-MM-dd";
            private final java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(pattern);

            @Override
            public Object stringToValue(String text) throws java.text.ParseException {
                return dateFormatter.parse(text);
            }

            @Override
            public String valueToString(Object value) throws java.text.ParseException {
                if (value == null) {
                    return "";
                }
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
        });
    }

    private static JSpinner buildTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        return spinner;
    }

    private static LocalDateTime buildDateTime(JDatePickerImpl picker, JSpinner timeSpinner) {
        Object value = picker.getModel().getValue();
        if (!(value instanceof Date)) {
            throw new IllegalArgumentException("Please select a start date.");
        }
        Date date = (Date) value;
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Date timeValue = (Date) timeSpinner.getValue();
        LocalTime localTime = timeValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
                .withSecond(0).withNano(0);

        return LocalDateTime.of(localDate, localTime);
    }
}
