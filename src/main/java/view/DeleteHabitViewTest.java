package view;

import interface_adapter.delete_habit.DeleteHabitController;
import interface_adapter.delete_habit.DeleteHabitState;
import interface_adapter.delete_habit.DeleteHabitViewModel;
import use_case.delete_habit.DeleteHabitInputBoundary;
import use_case.delete_habit.DeleteHabitInputData;

import javax.swing.*;

/**
 * 简单的手动测试类，用来查看 DeleteHabitView 的实际表现。
 * 运行 main 方法后，会弹出一个窗口，输入用户名和 habit 名称，
 * 点击 Delete，可以在控制台和弹窗中看到模拟结果。
 */
public class DeleteHabitViewTest {

    public static void main(String[] args) {
        // Swing 建议放在 EDT 中启动
        SwingUtilities.invokeLater(() -> {
            // 1. 创建 ViewModel
            DeleteHabitViewModel viewModel = new DeleteHabitViewModel("src/test");

            // 2. 创建 DeleteHabitView
            DeleteHabitView view = new DeleteHabitView(viewModel);

            // 3. 构造一个“假”的 Interactor，实现 DeleteHabitInputBoundary
            DeleteHabitInputBoundary fakeInteractor = new DeleteHabitInputBoundary() {
                @Override
                public void execute(DeleteHabitInputData inputData) {
                    // 这里先不连真正的 use case / DAO，只是模拟一下流程
                    String username = inputData.getUsername();
                    String habitName = inputData.getHabitName();

                    System.out.println("[FakeInteractor] 收到删除请求: username = "
                            + username + ", habitName = " + habitName);

                    // 模拟一个成功的删除，把结果写回 ViewModel 的 state
                    DeleteHabitState state = viewModel.getState();
                    state.setUsername(username);
                    state.setHabitName(habitName);
                    state.setSuccessMessage("模拟删除成功: 用户 '" + username
                            + "' 的习惯 '" + habitName + "'");
                    state.setErrorMessage(null);
                    viewModel.setState(state);
                    viewModel.firePropertyChanged();
                }
            };

            // 4. 用真正的 DeleteHabitController 把 fakeInteractor 装进去
            DeleteHabitController controller = new DeleteHabitController(fakeInteractor);
            view.setDeleteHabitController(controller);

            // 5. 把 View 放进一个 JFrame 里展示出来
            JFrame frame = new JFrame("DeleteHabitView Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.pack();
            frame.setLocationRelativeTo(null); // 居中
            frame.setVisible(true);
        });
    }
}
