package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class ViewTasksAndHabitsView extends JPanel implements ActionListener, PropertyChangeListener  {

    private final ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel;
    private ViewTasksAndHabitsController viewTasksAndHabitsController;
    private final String viewName = "view tasks and habits";
    private ViewManagerModel viewManagerModel;
    private LoggedInViewModel loggedInViewModel;

    private DefaultTableModel taskModel;
    private DefaultTableModel habitModel;

    private final JButton refreshButton;
    private final JButton exitButton;

    public ViewTasksAndHabitsView(ViewTasksAndHabitsViewModel viewTasksAndHabitsViewModel,
                                  ViewManagerModel viewManagerModel, LoggedInViewModel loggedInViewModel) {

        this.viewTasksAndHabitsViewModel = viewTasksAndHabitsViewModel;
        this.viewTasksAndHabitsViewModel.addPropertyChangeListener(this);
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;

        this.taskModel = new DefaultTableModel(viewTasksAndHabitsViewModel.taskCols, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        this.habitModel = new DefaultTableModel(viewTasksAndHabitsViewModel.habitCols, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        final JPanel TablePanel = new JPanel();
        final JPanel buttonPanel = new JPanel();

        this.exitButton = new JButton("Exit");
        this.refreshButton = new JButton("Refresh");

        buttonPanel.add(this.exitButton);
        buttonPanel.add(this.refreshButton);

        final JLabel TableLabel = new JLabel("Tasks and Habits");

        setLayout(new BorderLayout());

        JTable taskTable = new JTable(this.taskModel);
        taskTable.setFillsViewportHeight(true);
        JScrollPane taskScrollPane = new JScrollPane(taskTable);

        JTable habitTable = new JTable(this.habitModel);
        habitTable.setFillsViewportHeight(true);
        JScrollPane habitScrollPane = new JScrollPane(habitTable);

        add(TablePanel, BorderLayout.SOUTH);
        add(TableLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(1200, 500));
        tabbedPane.addTab("Tasks", taskScrollPane);
        tabbedPane.addTab("Habits", habitScrollPane);

        add(tabbedPane, BorderLayout.CENTER);


        this.exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(exitButton)) {
                    viewManagerModel.setState(loggedInViewModel.getViewName());
                    viewManagerModel.firePropertyChanged();
                }
            }
        });

        this.refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(refreshButton)) {

                    if (viewTasksAndHabitsController != null) {
                        viewTasksAndHabitsController.getFormattedTasksAndHabits(loggedInViewModel);
                    } else {
                        JOptionPane.showMessageDialog(ViewTasksAndHabitsView.this,
                                "Initialization in progress. Please wait a moment.",
                                "Loading", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });


        if (this.viewTasksAndHabitsController != null && this.loggedInViewModel != null) {
            this.viewTasksAndHabitsController.getFormattedTasksAndHabits(this.loggedInViewModel);
        }

    }

    /**
     * Required method for ActionListener, though typically handled via anonymous classes now.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        // Empty
    }

    /**
     * Clears and updates the JTables with fresh data from the ViewModel.
     */
    public void updateTable(ArrayList<ArrayList<String>> taskList, ArrayList<ArrayList<String>> habitList) {

        taskModel.setRowCount(0);
        habitModel.setRowCount(0);

        for (ArrayList<String> row : taskList) {
            Object[] rowData = row.toArray();
            taskModel.addRow(rowData);
        }

        for (ArrayList<String> row : habitList) {
            Object[] rowData = row.toArray();
            habitModel.addRow(rowData);
        }
    }

    /**
     * Setter for Dependency Injection, used by the main application builder to wire up the controller later.
     */
    public void setViewTasksAndHabitsController (ViewTasksAndHabitsController viewTasksAndHabitsController){
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
        if (this.loggedInViewModel != null) {
            this.viewTasksAndHabitsController.getFormattedTasksAndHabits(this.loggedInViewModel);
        }
    }

    public String getViewName () {
        return viewName;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Reacts to changes in the ViewModel state. This is how the table data is refreshed after a controller call.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            ViewTasksAndHabitsState state = (ViewTasksAndHabitsState) viewTasksAndHabitsViewModel.getState();


            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Data Loading Error", JOptionPane.ERROR_MESSAGE);
                state.setErrorMessage(null);
            }

            ArrayList<ArrayList<String>> formattedTasks = state.getFormattedTasks();
            ArrayList<ArrayList<String>> formattedHabits = state.getFormattedHabits();
            updateTable(formattedTasks, formattedHabits);
        }
    }
}