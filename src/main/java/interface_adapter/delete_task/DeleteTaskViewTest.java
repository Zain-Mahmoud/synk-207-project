package interface_adapter.delete_task;

import use_case.delete_task.DeleteTaskInputBoundary;
import use_case.delete_task.DeleteTaskInputData;
import view.DeleteTaskView;

import javax.swing.*;

/**
 * 简单的测试入口，用于在一个 JFrame 中展示 DeleteTaskView。
 * 依赖：
 *  - DeleteTaskView
 *  - DeleteTaskController
 *  - DeleteTaskViewModel
 *  - DeleteTaskState
 *  - use_case.delete_task.DeleteTaskInputBoundary
 *  - use_case.delete_task.DeleteTaskInputData
 */
public class DeleteTaskViewTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 准备 ViewModel
            DeleteTaskViewModel viewModel = new DeleteTaskViewModel("delete-task-view");

            // 2. 准备一个假的 Interactor（只做打印 + 更新 ViewModel，不连数据库）
            DeleteTaskInputBoundary dummyInteractor = new DummyDeleteTaskInteractor(viewModel);

            // 3. 准备 Controller
            DeleteTaskController controller = new DeleteTaskController(dummyInteractor);

            // 4. 准备 View（这里用户名和任务名随便写一个）
            String username = "testUser";
            String taskName = "Finish CSC207 Assignment";

            DeleteTaskView view = new DeleteTaskView(controller, viewModel, username, taskName);

            // 5. 放进 JFrame 里显示
            JFrame frame = new JFrame("DeleteTaskView Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.pack();
            frame.setLocationRelativeTo(null); // 居中
            frame.setVisible(true);
        });
    }

    /**
     * 一个简单的假的 Interactor，只用于测试 UI：
     * - 打印收到的输入
     * - 更新 ViewModel 的 state 显示“模拟删除成功”。
     */
    private static class DummyDeleteTaskInteractor implements DeleteTaskInputBoundary {

        private final DeleteTaskViewModel viewModel;

        public DummyDeleteTaskInteractor(DeleteTaskViewModel viewModel) {
            this.viewModel = viewModel;
        }

        @Override
        public void execute(DeleteTaskInputData inputData) {
            // 控制台打印参数，验证是否正确传递
            System.out.println("DummyDeleteTaskInteractor.excute 被调用:");
            System.out.println("  username = " + inputData.getUsername());
            System.out.println("  taskName = " + inputData.getTaskName());

            // 模拟“删除成功”：更新 ViewModel 的 state
            DeleteTaskState state = viewModel.getState();
            state.setUsername(inputData.getUsername());
            state.setTaskName(inputData.getTaskName());
            state.setSuccessMessage("模拟删除成功: " + inputData.getTaskName()
                    + " (用户: " + inputData.getUsername() + ")");
            state.setErrorMessage(null);

            viewModel.setState(state);
            viewModel.firePropertyChanged();
        }
    }
}
