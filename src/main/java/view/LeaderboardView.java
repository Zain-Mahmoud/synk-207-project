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

    private static final Color BACKGROUND_COLOR = new Color(242, 244, 248);
    private static final Color CARD_BORDER_COLOR = new Color(218, 222, 232);
    private static final Color PRIMARY_COLOR = new Color(88, 101, 242);
    private static final Color ACCENT_COLOR = new Color(46, 139, 87);
    private static final Color MUTED_TEXT_COLOR = new Color(120, 130, 150);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    private final String viewName = "leaderboard";
    private final ViewLeaderboardViewModel viewLeaderboardViewModel;
    private ViewLeaderboardController viewLeaderboardController;
    private ViewManagerModel viewManagerModel;

    private final JLabel title = new JLabel(ViewLeaderboardViewModel.TITLE_LABEL, SwingConstants.LEFT);
    private final JLabel subtitle = new JLabel("See who’s keeping their habits going the longest.", SwingConstants.LEFT);
    private final JLabel statsLabel = new JLabel("", SwingConstants.RIGHT);

    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton backButton;
    private final JLabel errorLabel = new JLabel("", SwingConstants.LEFT);

    public LeaderboardView(ViewLeaderboardViewModel viewLeaderboardViewModel) {
        this.viewLeaderboardViewModel = viewLeaderboardViewModel;
        this.viewLeaderboardViewModel.addPropertyChangeListener(this);

        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 32, 24, 32));

        setupHeaderText();
        setupTable();
        setupButtons();
        setupErrorLabel();

        JPanel headerPanel = createHeaderPanel();

        JPanel cardPanel = new JPanel(new BorderLayout(0, 16));
        cardPanel.setBackground(Color.WHITE);
        Border cardBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, CARD_BORDER_COLOR),
                new EmptyBorder(20, 20, 16, 20)
        );
        cardPanel.setBorder(cardBorder);
        cardPanel.add(createTablePanel(), BorderLayout.CENTER);
        cardPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centerWrapper.add(cardPanel, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void setupHeaderText() {
        Font baseFont = UIManager.getFont("Label.font");
        if (baseFont == null) {
            baseFont = new Font("SansSerif", Font.PLAIN, 14);
        }

        Font titleFont = baseFont.deriveFont(Font.BOLD, 22f);
        Font subtitleFont = baseFont.deriveFont(Font.PLAIN, 12f);

        title.setFont(titleFont);
        title.setForeground(new Color(45, 57, 82));

        subtitle.setFont(subtitleFont);
        subtitle.setForeground(MUTED_TEXT_COLOR);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(title);
        left.add(Box.createRigidArea(new Dimension(0, 4)));
        left.add(subtitle);

        headerPanel.add(left, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

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
        leaderboardTable.setPreferredScrollableViewportSize(new Dimension(600, 360));
        leaderboardTable.setFillsViewportHeight(true);
        leaderboardTable.setRowSelectionAllowed(false);
        leaderboardTable.setRowHeight(34);
        leaderboardTable.setGridColor(new Color(225, 228, 236));
        leaderboardTable.setShowGrid(true);
        leaderboardTable.setIntercellSpacing(new Dimension(0, 0));

        Font baseFont = UIManager.getFont("Table.font");
        if (baseFont == null) {
            baseFont = new Font("SansSerif", Font.PLAIN, 13);
        }
        leaderboardTable.setFont(baseFont.deriveFont(Font.PLAIN, 13f));

        styleTableHeader(baseFont);
        styleTableCells(baseFont);
    }

    private void styleTableHeader(Font baseFont) {
        JTableHeader header = leaderboardTable.getTableHeader();
        header.setFont(baseFont.deriveFont(Font.BOLD, 13f));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);
    }

    private void styleTableCells(Font baseFont) {
        leaderboardTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(232, 235, 252));
                } else {
                    if (row == 0) {
                        c.setBackground(new Color(246, 237, 208));
                    } else if (row == 1) {
                        c.setBackground(new Color(199, 199, 218));
                    } else if (row == 2) {
                        c.setBackground(new Color(234, 220, 208, 255));
                    } else if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(250, 250, 252));
                    }
                }

                c.setForeground(Color.BLACK);
                c.setFont(baseFont.deriveFont(Font.PLAIN, 13f));

                if (column == 0) {
                    int rankValue = -1;
                    if (value instanceof Number) {
                        rankValue = ((Number) value).intValue();
                    }
                    if (rankValue == 1) {
                        setText("🥇 " + rankValue);
                    } else if (rankValue == 2) {
                        setText("🥈 " + rankValue);
                    } else if (rankValue == 3) {
                        setText("🥉 " + rankValue);
                    }
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(new Color(80, 80, 80));
                    setFont(baseFont.deriveFont(Font.BOLD, 13f));
                } else if (column == 2) {
                    int streakValue = -1;
                    if (value instanceof Number) {
                        streakValue = ((Number) value).intValue();
                    }
                    if (streakValue > 0) {
                        setForeground(ACCENT_COLOR.darker());
                        setFont(baseFont.deriveFont(Font.BOLD, 13f));
                    }
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                setBorder(new EmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
    }

    private void setupButtons() {
        refreshButton = createPrimaryButton(ViewLeaderboardViewModel.REFRESH_BUTTON_LABEL);
        refreshButton.addActionListener(this);

        backButton = createSecondaryButton("Back");
        backButton.addActionListener(evt -> {
            if (evt.getSource().equals(backButton) && viewManagerModel != null) {
                viewManagerModel.setState("logged in");
                viewManagerModel.firePropertyChanged();
            }
        });
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        Font baseFont = UIManager.getFont("Button.font");
        if (baseFont == null) {
            baseFont = new Font("SansSerif", Font.PLAIN, 13);
        }
        button.setFont(baseFont.deriveFont(Font.PLAIN, 13f));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        Font baseFont = UIManager.getFont("Button.font");
        if (baseFont == null) {
            baseFont = new Font("SansSerif", Font.PLAIN, 13);
        }
        button.setFont(baseFont.deriveFont(Font.PLAIN, 13f));
        button.setForeground(PRIMARY_COLOR);
        button.setBackground(new Color(246, 247, 252));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 221), 1, true),
                new EmptyBorder(8, 18, 8, 18)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(237, 239, 248));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(246, 247, 252));
            }
        });

        return button;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        statsLabel.setForeground(MUTED_TEXT_COLOR);
        Font baseFont = UIManager.getFont("Label.font");
        if (baseFont == null) {
            baseFont = new Font("SansSerif", Font.PLAIN, 12);
        }
        statsLabel.setFont(baseFont.deriveFont(Font.PLAIN, 12f));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        leftPanel.setOpaque(false);
        leftPanel.add(errorLabel);
        leftPanel.add(statsLabel);

        bottomPanel.add(leftPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        return bottomPanel;
    }

    private void setupErrorLabel() {
        Font baseFont = UIManager.getFont("Label.font");
        if (baseFont == null) {
            baseFont = new Font("SansSerif", Font.PLAIN, 12);
        }
        errorLabel.setFont(baseFont.deriveFont(Font.PLAIN, 12f));
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
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
            errorLabel.setForeground(ERROR_COLOR);
            tableModel.setRowCount(0);
            statsLabel.setText("Participants: 0");
        } else {
            updateTable(state.getLeaderboardEntries());
        }
    }

    private void updateTable(List<Map<String, Object>> leaderboardEntries) {
        tableModel.setRowCount(0);

        if (leaderboardEntries.isEmpty()) {
            errorLabel.setText("No leaderboard data available. Create some habits to see rankings!");
            errorLabel.setForeground(MUTED_TEXT_COLOR);
            statsLabel.setText("Participants: 0");
            return;
        }

        errorLabel.setText("");
        errorLabel.setForeground(MUTED_TEXT_COLOR);
        statsLabel.setText("Participants: " + leaderboardEntries.size());

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
