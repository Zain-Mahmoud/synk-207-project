package view;

import interface_adapter.ViewManagerModel;
import interface_adapter.leaderboard.ViewLeaderboardController;
import interface_adapter.leaderboard.ViewLeaderboardState;
import interface_adapter.leaderboard.ViewLeaderboardViewModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    private static final Font TABLE_HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font TABLE_CELL_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(236, 240, 241);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);

    private final String viewName = "leaderboard";
    private final ViewLeaderboardViewModel viewLeaderboardViewModel;
    private ViewLeaderboardController viewLeaderboardController;
    private ViewManagerModel viewManagerModel;

    private final JLabel title = new JLabel(ViewLeaderboardViewModel.TITLE_LABEL, SwingConstants.CENTER);
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton backButton;
    private final JLabel errorLabel = new JLabel("", SwingConstants.CENTER);

    public LeaderboardView(ViewLeaderboardViewModel viewLeaderboardViewModel) {
        this.viewLeaderboardViewModel = viewLeaderboardViewModel;
        this.viewLeaderboardViewModel.addPropertyChangeListener(this);

        setupUI();
    }

    private void setupUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(SECONDARY_COLOR);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        setupTitle();
        setupTable();
        setupButtons();
        setupErrorLabel();

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(SECONDARY_COLOR);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);

        this.add(title, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private void setupTitle() {
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR.darker());
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        Border lineBorder = BorderFactory.createLineBorder(PRIMARY_COLOR, 2);
        Border marginBorder = new EmptyBorder(10, 10, 10, 10);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(lineBorder, marginBorder));

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void setupTable() {
        String[] columnNames = {"Rank", "Username", "Max Streak"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setPreferredScrollableViewportSize(new Dimension(600, 400));
        leaderboardTable.setFillsViewportHeight(true);
        leaderboardTable.setRowSelectionAllowed(false);
        leaderboardTable.setRowHeight(35);
        leaderboardTable.setFont(TABLE_CELL_FONT);
        leaderboardTable.setGridColor(new Color(220, 220, 220));
        leaderboardTable.setShowGrid(true);
        leaderboardTable.setIntercellSpacing(new Dimension(0, 0));

        styleTableHeader();
        styleTableCells();
    }

    private void styleTableHeader() {
        JTableHeader header = leaderboardTable.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);
    }

    private void styleTableCells() {
        leaderboardTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                c.setForeground(Color.BLACK);

                if (column == 0) {
                    Integer rank = (Integer) value;
                    if (rank != null && rank <= 3) {
                        c.setForeground(PRIMARY_COLOR.darker());
                        c.setFont(new Font("SansSerif", Font.BOLD, 14));
                    }
                } else if (column == 2) {
                    Integer streak = (Integer) value;
                    if (streak != null && streak > 0) {
                        c.setForeground(ACCENT_COLOR.darker());
                        c.setFont(new Font("SansSerif", Font.BOLD, 13));
                    }
                }

                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
    }

    private void setupButtons() {
        refreshButton = createStyledButton(ViewLeaderboardViewModel.REFRESH_BUTTON_LABEL);
        refreshButton.addActionListener(this);

        backButton = createStyledButton("Back");
        backButton.addActionListener(evt -> {
            if (evt.getSource().equals(backButton) && viewManagerModel != null) {
                viewManagerModel.setState("logged in");
                viewManagerModel.firePropertyChanged();
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY_COLOR.darker());
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                new EmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(PRIMARY_COLOR.darker());
            }
        });

        return button;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(SECONDARY_COLOR);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        buttonPanel.add(errorLabel);
        return buttonPanel;
    }

    private void setupErrorLabel() {
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
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
            errorLabel.setForeground(new Color(231, 76, 60));
            tableModel.setRowCount(0);
        } else {
            updateTable(state.getLeaderboardEntries());
        }
    }

    private void updateTable(List<Map<String, Object>> leaderboardEntries) {
        tableModel.setRowCount(0);

        if (leaderboardEntries.isEmpty()) {
            errorLabel.setText("No leaderboard data available. Create some habits to see rankings!");
            errorLabel.setForeground(new Color(149, 165, 166));
            return;
        }

        errorLabel.setText("");

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

        leaderboardTable.repaint();
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

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
        if (viewManagerModel != null) {
            viewManagerModel.addPropertyChangeListener(evt -> {
                if ("state".equals(evt.getPropertyName())) {
                    String currentView = (String) evt.getNewValue();
                    if (viewName.equals(currentView) && viewLeaderboardController != null) {
                        viewLeaderboardController.execute();
                    }
                }
            });
        }
    }
}

