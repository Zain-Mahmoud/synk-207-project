package view;

import interface_adapter.delete_task.DeleteTaskController;
import interface_adapter.delete_task.DeleteTaskState;
import interface_adapter.delete_task.DeleteTaskViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * DeleteTask 的界面：
 * - 显示删除确认信息（带 username + taskName）
 * - 提供 Delete / Cancel 按钮
 * - 监听 ViewModel 的状态变化，更新成功 / 失败消息
 */
public class DeleteTaskView extends JPanel {

    private final DeleteTaskController controller;
    private final DeleteTaskViewModel viewModel;

    // 组件
    private final JLabel titleLabel;
    private final JLabel confirmLabel;
    private final JLabel successLabel;
    private final JLabel errorLabel;
    private final JButton deleteButton;
    private final JButton cancelButton;

    public DeleteTaskView(DeleteTaskController controller,
                          DeleteTaskViewModel viewModel,
                          String username,
                          String taskName) {

        this.controller = controller;
        this.viewModel = viewModel;

        // 先把 username / taskName 写入 state
        DeleteTaskState state = viewModel.getState();
        state.setUsername(username);
        state.setTaskName(taskName);
        state.setSuccessMessage(null);
        state.setErrorMessage(null);
        viewModel.setState(state);

        // 初始化组件
        titleLabel = new JLabel(DeleteTaskViewModel.TITLE_LABEL, SwingConstants.CENTER);
        confirmLabel = new JLabel("", SwingConstants.CENTER);
        successLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel = new JLabel("", SwingConstants.CENTER);

        deleteButton = new JButton(DeleteTaskViewModel.DELETE_BUTTON_LABEL);
        cancelButton = new JButton(DeleteTaskViewModel.CANCEL_BUTTON_LABEL);

        // 简单设置一下样式（可按需调整）
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        successLabel.setForeground(new Color(0, 128, 0)); // 绿色
        errorLabel.setForeground(Color.RED);

        setLayout(new BorderLayout(10, 10));

        // 顶部标题
        add(titleLabel, BorderLayout.NORTH);

        // 中间：确认文本 + 消息
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 1, 5, 5));
        centerPanel.add(confirmLabel);
        centerPanel.add(successLabel);
        centerPanel.add(errorLabel);
        add(centerPanel, BorderLayout.CENTER);

        // 底部按钮行
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 初始化一次界面文本
        updateFromState(viewModel.getState());

        // 绑定按钮事件
        addListeners();

        // 监听 ViewModel 变化（例如 Presenter 更新了 state）
        viewModel.addPropertyChangeListener(evt -> {
            // 每次有变化，就从 ViewModel 重新取最新 state 更新 UI
            DeleteTaskState newState = viewModel.getState();
            updateFromState(newState);
        });
    }

    /**
     * 根据当前 state 更新界面显示
     */
    private void updateFromState(DeleteTaskState state) {
        String username = state.getUsername();
        String taskName = state.getTaskName();

        // 基础确认文本先按 ViewModel 常量来
        // CONFIRM_MESSAGE = "Are you sure you want to delete task '%s'?"
        String baseConfirm = String.format(DeleteTaskViewModel.CONFIRM_MESSAGE,
                taskName == null ? "" : taskName);

        // 如果有 username，就在后面补一句
        if (username != null && !username.isEmpty()) {
            confirmLabel.setText(baseConfirm + " for user '" + username + "'?");
        } else {
            confirmLabel.setText(baseConfirm);
        }

        // 成功 / 失败消息
        successLabel.setText(state.getSuccessMessage() == null ? "" : state.getSuccessMessage());
        errorLabel.setText(state.getErrorMessage() == null ? "" : state.getErrorMessage());
    }

    /**
     * 绑定按钮事件
     */
    private void addListeners() {
        // Delete：调用 controller.execute(username, taskName)
        deleteButton.addActionListener(e -> {
            DeleteTaskState state = viewModel.getState();
            String username = state.getUsername();
            String taskName = state.getTaskName();
            controller.execute(username, taskName);
        });

        // Cancel：这里只做简单处理（清空消息），导航交给上层 ViewManager
        cancelButton.addActionListener(e -> {
            DeleteTaskState state = viewModel.getState();
            state.setSuccessMessage(null);
            state.setErrorMessage(null);
            viewModel.setState(state);
            viewModel.firePropertyChanged();
        });
    }
}
