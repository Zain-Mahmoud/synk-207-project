package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;

import interface_adapter.logged_in.LoggedInViewModel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.awt.*;

public class ViewTasksAndHabitsView extends JPanel implements ActionListener, PropertyChangeListener  {

    private final ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel;
    private ViewTasksAndHabitsController viewTasksAndHabitsController;
    private final String viewName = "view tasks and habits";
    private ViewManagerModel viewManagerModel;
    private LoggedInViewModel loggedInViewModel;

    private DefaultTableModel taskModel; // Now initialized in constructor
    private DefaultTableModel habitModel; // Now initialized in constructor

    private JButton refreshButton;
    private JButton exitButton;

    // Table Column Headers for initialization
    private static final String[] TASK_HEADERS = {"Name", "Deadline", "Start Time", "Group", "Priority", "Status", "Description"};
    private static final String[] HABIT_HEADERS = {"Name", "Start Date", "Frequency", "Group", "Priority", "Status", "Description"};

    public ViewTasksAndHabitsView(ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel,
                                  ViewManagerModel viewManagerModel, LoggedInViewModel loggedInViewModel, ViewTasksAndHabitsController viewTasksAndHabitsController) {

        this.viewTasksAndHabitsViewModel = viewTasksAndHabitsViewModel;
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;

        // FIX: Initialize taskModel and habitModel to prevent NullPointerException
        this.taskModel = new DefaultTableModel(TASK_HEADERS, 0);
        this.habitModel = new DefaultTableModel(HABIT_HEADERS, 0);

        this.viewTasksAndHabitsViewModel.addPropertyChangeListener(this);

        // --- Layout Setup ---
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Your Tasks and Habits", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // --- Tasks Table Setup ---
        JTable taskTable = new JTable(taskModel);
        taskTable.setFillsViewportHeight(true);
        JScrollPane taskScrollPane = new JScrollPane(taskTable);

        // Add Table Model Listener for modifying tasks
        taskModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    Object changedValue = taskModel.getValueAt(row, col);

                    String taskName = taskModel.getValueAt(row, 0).toString();

                    viewTasksAndHabitsController.execute("task", col, taskName, changedValue);
                }
            }
        });

        // --- Habits Table Setup ---
        JTable habitTable = new JTable(habitModel);
        habitTable.setFillsViewportHeight(true);
        JScrollPane habitScrollPane = new JScrollPane(habitTable);

        // Add Table Model Listener for modifying habits
        habitModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    Object changedValue = habitModel.getValueAt(row, col);

                    String habitName = habitModel.getValueAt(row, 0).toString();

                    viewTasksAndHabitsController.execute("habit", col, habitName, changedValue);
                }
            }
        });

        // Use a JTabbedPane to display Tasks and Habits
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tasks", taskScrollPane);
        tabbedPane.addTab("Habits", habitScrollPane);

        add(tabbedPane, BorderLayout.CENTER);

        // --- Buttons Panel ---
        refreshButton = new JButton("Refresh Data");
        exitButton = new JButton("Back to Main");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(refreshButton)) {
                    // Call the use case to fetch and format data
                    viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // Assuming "logged in" is the main view name
                viewManagerModel.setState(loggedInViewModel.getViewName());
                viewManagerModel.firePropertyChanged();
            }
        });
    }

    // This method handles refreshing the table data
    public void updateTable(ArrayList<ArrayList<String>> taskList, ArrayList<ArrayList<String>> habitList) {
        // Clear existing rows
        taskModel.setRowCount(0);
        habitModel.setRowCount(0);

        // Add new task rows
        for (ArrayList<String> row : taskList) {
            Object[] rowData = row.toArray();
            taskModel.addRow(rowData);
        }

        // Add new habit rows
        for (ArrayList<String> row : habitList) {
            Object[] rowData = row.toArray();
            habitModel.addRow(rowData);
        }
    }

    public void setViewTasksAndHabitsController (ViewTasksAndHabitsController viewTasksAndHabitsController){
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
    }

    public String getViewName () {
        return viewName;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Not used as listeners are attached directly in the constructor
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            ViewTasksAndHabitsState state = (ViewTasksAndHabitsState) viewTasksAndHabitsViewModel.getState();
            // Handle error message if present
            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
                state.setErrorMessage(null); // Clear the error after showing
            }
            ArrayList<ArrayList<String>> formattedTasks = state.getFormattedTasks();
            ArrayList<ArrayList<String>> formattedHabits = state.getFormattedHabits();
            updateTable(formattedTasks, formattedHabits);
        }
    }
}