package view;

import interface_adapter.leaderboard.ViewLeaderboardController;
import interface_adapter.leaderboard.ViewLeaderboardState;
import interface_adapter.leaderboard.ViewLeaderboardViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

/**
 * The View for displaying the Habit Streak Leaderboard.
 */
public class LeaderboardView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "leaderboard";
    private final ViewLeaderboardViewModel viewLeaderboardViewModel;
    private ViewLeaderboardController viewLeaderboardController;

    private final JLabel title = new JLabel(ViewLeaderboardViewModel.TITLE_LABEL);
    private final JTable leaderboardTable;
    private final DefaultTableModel tableModel;
    private final JButton refreshButton;
    private final JLabel errorLabel = new JLabel();

    public LeaderboardView(ViewLeaderboardViewModel viewLeaderboardViewModel) {
        this.viewLeaderboardViewModel = viewLeaderboardViewModel;
        this.viewLeaderboardViewModel.addPropertyChangeListener(this);
        
        // Auto-load leaderboard when view is created
        // Controller will be set later, so we'll load on first property change or when controller is set

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 18));

        // Create table with columns: Rank, Username, Max Streak
        String[] columnNames = {"Rank", "Username", "Max Streak"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setPreferredScrollableViewportSize(new Dimension(500, 300));
        leaderboardTable.setFillsViewportHeight(true);
        leaderboardTable.setRowSelectionAllowed(false);

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);

        refreshButton = new JButton(ViewLeaderboardViewModel.REFRESH_BUTTON_LABEL);
        refreshButton.addActionListener(this);

        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(scrollPane);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(refreshButton);
        this.add(errorLabel);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(refreshButton)) {
            if (viewLeaderboardController != null) {
                viewLeaderboardController.execute();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        ViewLeaderboardState state = (ViewLeaderboardState) evt.getNewValue();
        
        if (state.getErrorMessage() != null) {
            errorLabel.setText(state.getErrorMessage());
            // Clear table on error
            tableModel.setRowCount(0);
        } else {
            errorLabel.setText("");
            updateTable(state.getLeaderboardEntries());
        }
    }

    private void updateTable(List<Map<String, Object>> leaderboardEntries) {
        tableModel.setRowCount(0); // Clear existing rows

        for (Map<String, Object> entry : leaderboardEntries) {
            Integer rank = (Integer) entry.get("rank");
            String username = (String) entry.get("username");
            Integer maxStreak = (Integer) entry.get("maxStreak");

            tableModel.addRow(new Object[]{
                    rank != null ? rank : "",
                    username != null ? username : "",
                    maxStreak != null ? maxStreak : 0
            });
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewLeaderboardController(ViewLeaderboardController controller) {
        this.viewLeaderboardController = controller;
        // Auto-load data when controller is set
        if (controller != null) {
            controller.execute();
        }
    }
}

