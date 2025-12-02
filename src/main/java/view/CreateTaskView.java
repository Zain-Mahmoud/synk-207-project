package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.create_task.CreateTaskController;
import interface_adapter.create_task.CreateTaskState;
import interface_adapter.create_task.CreateTaskViewModel;

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
 * View for creating a new Task.
 * All business attributes are entered by the user, while the username
 * is taken from the LoggedInViewModel.
 */
public class CreateTaskView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "create task";

    private final CreateTaskViewModel viewModel;
    private final ViewManagerModel viewManagerModel;
    private final LoggedInViewModel loggedInViewModel;

    private CreateTaskController createTaskController;

    private final JTextField taskNameField = new JTextField(15);
    private final JTextField descriptionField = new JTextField(15);
    private final UtilDateModel startDateModel = buildDateModel();
    private final JDatePickerImpl startDatePicker = buildDatePicker(startDateModel);
    private final JSpinner startTimeSpinner = buildTimeSpinner();
    private final UtilDateModel deadlineDateModel = buildDateModel();
    private final JDatePickerImpl deadlineDatePicker = buildDatePicker(deadlineDateModel);
    private final JSpinner deadlineTimeSpinner = buildTimeSpinner();
    private final JTextField taskGroupField = new JTextField(15);
    private final JTextField priorityField = new JTextField(15);

    private final JButton createButton = new JButton("Create");
    private final JButton cancelButton = new JButton("Cancel");

    public CreateTaskView(CreateTaskViewModel viewModel,
                          ViewManagerModel viewManagerModel,
                          LoggedInViewModel loggedInViewModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;

        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JLabel title = new JLabel(CreateTaskViewModel.TITLE_LABEL, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Task name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.TASK_NAME_LABEL + ":"), gbc);
        gbc.gridx = 1;
        formPanel.add(taskNameField, gbc);
        row++;

        // Description
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.DESCRIPTION_LABEL + ":"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);
        row++;

        // Start time
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

        // Deadline
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.DEADLINE_LABEL + " Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(deadlineDatePicker, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.DEADLINE_LABEL + " Time:"), gbc);
        gbc.gridx = 1;
        formPanel.add(deadlineTimeSpinner, gbc);
        row++;

        // Task group
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.TASK_GROUP_LABEL + ":"), gbc);
        gbc.gridx = 1;
        formPanel.add(taskGroupField, gbc);
        row++;

        // Priority
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel(CreateTaskViewModel.PRIORITY_LABEL + " (integer):"), gbc);
        gbc.gridx = 1;
        formPanel.add(priorityField, gbc);
        row++;

        add(formPanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(createButton);
        buttons.add(cancelButton);
        add(buttons, BorderLayout.SOUTH);

        // Listeners
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
        if (createTaskController == null) {
            JOptionPane.showMessageDialog(this,
                    "Create Task controller not initialized.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = loggedInViewModel.getState().getUsername();
        String taskName = taskNameField.getText().trim();
        String description = descriptionField.getText().trim();
        LocalDateTime startTime = buildDateTime(startDatePicker, startTimeSpinner);
        LocalDateTime deadline = buildDateTime(deadlineDatePicker, deadlineTimeSpinner);
        String group = taskGroupField.getText().trim();
        String priorityText = priorityField.getText().trim();

        if (taskName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task name cannot be empty.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (deadline.isBefore(startTime)) {
                JOptionPane.showMessageDialog(this,
                        "Deadline must be after the start time.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int priority = Integer.parseInt(priorityText);

            createTaskController.execute(username, taskName, description,
                    startTime, deadline, group, false, priority);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input: " + ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
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
            throw new IllegalArgumentException("Please select a date.");
        }
        Date date = (Date) value;
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Date timeValue = (Date) timeSpinner.getValue();
        LocalTime localTime = timeValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
                .withSecond(0).withNano(0);

        return LocalDateTime.of(localDate, localTime);
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
        CreateTaskState state = viewModel.getState();
        if (state.getErrorMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else if (state.getSuccessMessage() != null) {
            JOptionPane.showMessageDialog(this, state.getSuccessMessage(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            taskNameField.setText("");
            descriptionField.setText("");
            resetDateModel(startDateModel);
            startTimeSpinner.setValue(new Date());
            resetDateModel(deadlineDateModel);
            deadlineTimeSpinner.setValue(new Date());
            taskGroupField.setText("");
            priorityField.setText("");

            viewManagerModel.setState("view tasks and habits");
            viewManagerModel.firePropertyChanged();
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setCreateTaskController(CreateTaskController controller) {
        this.createTaskController = controller;
    }
}
