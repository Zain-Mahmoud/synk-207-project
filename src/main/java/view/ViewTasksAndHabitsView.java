package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsController;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;

public class ViewTasksAndHabitsView extends JPanel implements ActionListener, PropertyChangeListener {
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

        this.taskModel = new DefaultTableModel(viewTasksAndHabitsViewModel.TASKCOLS, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        this.habitModel = new DefaultTableModel(viewTasksAndHabitsViewModel.HABITCOLS, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        final JPanel tablePanel = new JPanel();
        final JPanel buttonPanel = new JPanel();

        this.exitButton = new JButton(viewTasksAndHabitsViewModel.EXITBUTTONLABEL);
        this.refreshButton = new JButton(viewTasksAndHabitsViewModel.REFRESHBUTTONLABEL);

        buttonPanel.add(this.exitButton);
        buttonPanel.add(this.refreshButton);

        final JLabel mainLabel = new JLabel("Tasks and Habits");

        setLayout(new BorderLayout());

        final JTable taskTable = new JTable(this.taskModel);
        taskTable.setFillsViewportHeight(true);
        final JScrollPane taskScrollPane = new JScrollPane(taskTable);

        final JTable habitTable = new JTable(this.habitModel);
        habitTable.setFillsViewportHeight(true);
        final JScrollPane habitScrollPane = new JScrollPane(habitTable);

        add(tablePanel, BorderLayout.SOUTH);
        add(mainLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.EAST);

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(viewTasksAndHabitsViewModel.VIEWWIDTH,
                viewTasksAndHabitsViewModel.VIEWHEIGHT));
        tabbedPane.addTab(viewTasksAndHabitsViewModel.TASKTABTITLE, taskScrollPane);
        tabbedPane.addTab(viewTasksAndHabitsViewModel.HABITTABTITLE, habitScrollPane);

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
                    }
                    else {
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
     * Required method for ActionListener, but unused in this case.
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
    }

    /**
     * Clears and updates the JTables with fresh data from the ViewModel.
     * @param taskList The list of tasks of the current user
     * @param habitList The list of habits of the current user
     */
    public void updateTable(ArrayList<ArrayList<String>> taskList, ArrayList<ArrayList<String>> habitList) {

        taskModel.setRowCount(0);
        habitModel.setRowCount(0);

        for (ArrayList<String> row : taskList) {
            final Object[] rowData = row.toArray();
            taskModel.addRow(rowData);
        }

        for (ArrayList<String> row : habitList) {
            final Object[] rowData = row.toArray();
            habitModel.addRow(rowData);
        }
    }

    /**
     * A setter for the controller of this use case, which also calls the controller to update the use case's state
     * with the formatted tasks and habits.
     * @param viewTasksAndHabitsController the controller for this use case
     */
    public void setViewTasksAndHabitsController(ViewTasksAndHabitsController viewTasksAndHabitsController) {
        this.viewTasksAndHabitsController = viewTasksAndHabitsController;
        if (this.loggedInViewModel != null) {
            this.viewTasksAndHabitsController.getFormattedTasksAndHabits(this.loggedInViewModel);
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final ViewTasksAndHabitsState state = (ViewTasksAndHabitsState) viewTasksAndHabitsViewModel.getState();

            if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
                JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Data Loading Error",
                        JOptionPane.ERROR_MESSAGE);
                state.setErrorMessage(null);
            }

            final ArrayList<ArrayList<String>> formattedTasks = state.getFormattedTasks();
            final ArrayList<ArrayList<String>> formattedHabits = state.getFormattedHabits();
            updateTable(formattedTasks, formattedHabits);
        }
    }
}
