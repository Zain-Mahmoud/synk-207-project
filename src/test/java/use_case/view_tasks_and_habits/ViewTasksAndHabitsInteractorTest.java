package use_case.view_tasks_and_habits;

import data_access.InMemoryHabitDataAccessObject;
import data_access.InMemoryTaskDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entities.*;
import interface_adapter.ViewManagerModel;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsPresenter;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsState;
import interface_adapter.view_tasks_and_habits.ViewTasksAndHabitsViewModel;
import interface_adapter.logged_in.LoggedInViewModel;
import org.junit.jupiter.api.Test;
import use_case.gateways.HabitGateway;
import use_case.gateways.TaskGateway;
import use_case.gateways.UserGateway;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ViewTasksAndHabitsInteractorTest {

    @Test
    void SuccessTest() {

        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        UserGateway userGateway = new InMemoryUserDataAccessObject();

        LoggedInViewModel loggedInViewModel = new LoggedInViewModel();

        ViewTasksAndHabitsViewModel testViewModel = new ViewTasksAndHabitsViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        TestPresenter presenter = new TestPresenter(viewManagerModel, testViewModel);

        TaskBuilder taskBuilder = new TaskBuilder();
        HabitBuilder habitBuilder = new HabitBuilder();

        Task task1 = taskBuilder
                .setTaskGroup("Problem set")
                .setTaskName("Problem Set")
                .setStartTime(LocalDateTime.parse("2025-11-26T00:00:00"))
                .setDeadline(LocalDateTime.parse("2025-11-27T00:00:00"))
                .setStatus(false)
                .setPriority(2)
                .setDescription("Finishing a problem set")
                .build();

        Task task2 = taskBuilder
                .setTaskGroup("project")
                .setTaskName("project")
                .setStartTime(LocalDateTime.parse("2025-11-26T00:00:00"))
                .setDeadline(LocalDateTime.parse("2025-11-27T00:00:00"))
                .setStatus(true)
                .setPriority(2)
                .setDescription("Finishing a project")
                .build();

        Task emptyTask = taskBuilder.build();

        Habit habit1 = habitBuilder.setHabitName("Not programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse("2025-11-27T00:00:00"))
                .setPriority(1)
                .setStatus(true)
                .setStreakCount(1)
                .setDescription("Not doing some programming")
                .build();

        Habit habit2 = habitBuilder.setHabitName("Programming")
                .setHabitGroup("Programming")
                .setFrequency(1)
                .setStartDateTime(LocalDateTime.parse("2025-11-27T00:00:00"))
                .setPriority(4)
                .setStatus(false)
                .setStreakCount(10)
                .setDescription("Doing some programming")
                .build();

        UserFactory factory = new UserFactory();
        User arya = factory.create("arya", "123");
        userGateway.save(arya);

        ViewTasksAndHabitsInputData inputData = new ViewTasksAndHabitsInputData("arya");

        taskGateway.addTask("arya", task1);
        taskGateway.addTask("arya", task2);
        habitGateway.addHabit("arya", habit1);
        habitGateway.addHabit("arya", habit2);

        ViewTasksAndHabitsInteractor interactor =
                new ViewTasksAndHabitsInteractor(taskGateway, habitGateway, presenter);

        ArrayList<String> task1List = new ArrayList<>();
        task1List.add("Problem Set");
        task1List.add("26 November, 2025 00:00");
        task1List.add("27 November, 2025 00:00");
        task1List.add("Problem set");
        task1List.add("Incomplete");
        task1List.add("2");
        task1List.add("Finishing a problem set");

        ArrayList<String> task2List = new ArrayList<>();
        task2List.add("project");
        task2List.add("26 November, 2025 00:00");
        task2List.add("27 November, 2025 00:00");
        task2List.add("project");
        task2List.add("Complete");
        task2List.add("2");
        task2List.add("Finishing a project");

        ArrayList<String> habit1List = new ArrayList<>();
        habit1List.add("Not programming");
        habit1List.add("27 November, 2025 00:00");
        habit1List.add("1");
        habit1List.add("Programming");
        habit1List.add("1");
        habit1List.add("1");
        habit1List.add("Complete");
        habit1List.add("Not programming started on 2025-11-27T00:00");

        ArrayList<String> habit2List = new ArrayList<>();
        habit2List.add("Programming");
        habit2List.add("27 November, 2025 00:00");
        habit2List.add("1");
        habit2List.add("Programming");
        habit2List.add("10");
        habit2List.add("4");
        habit2List.add("Incomplete");
        habit2List.add("Programming started on 2025-11-27T00:00");

        interactor.getFormattedTasksAndHabits(inputData);

        ViewTasksAndHabitsState resultState = testViewModel.getState();
        assertNotNull(resultState);

        assertEquals(2, resultState.getFormattedTasks().size());
        assertEquals(2, resultState.getFormattedHabits().size());

        assertEquals(task1List, resultState.getFormattedTasks().get(0));
        assertEquals(task2List, resultState.getFormattedTasks().get(1));

        assertEquals(habit1List, resultState.getFormattedHabits().get(0));
        assertEquals(habit2List, resultState.getFormattedHabits().get(1));
    }

    @Test
    void FailTest() {
        TaskGateway taskGateway = new InMemoryTaskDataAccessObject();
        HabitGateway habitGateway = new InMemoryHabitDataAccessObject();
        UserGateway userGateway = new InMemoryUserDataAccessObject();

        LoggedInViewModel loggedInViewModel = new LoggedInViewModel();

        ViewTasksAndHabitsViewModel testViewModel = new ViewTasksAndHabitsViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        TestPresenter presenter = new TestPresenter(viewManagerModel, testViewModel);

        TaskBuilder taskBuilder = new TaskBuilder();
        HabitBuilder habitBuilder = new HabitBuilder();

        Task emptyTask = taskBuilder
                        .setTaskName("Empty Task")
                        .build();

        UserFactory factory = new UserFactory();
        User arya = factory.create("arya", "123");
        userGateway.save(arya);

        taskGateway.addTask("arya", emptyTask);

        ViewTasksAndHabitsInputData inputData = new ViewTasksAndHabitsInputData("arya");

        ViewTasksAndHabitsInteractor interactor =
                new ViewTasksAndHabitsInteractor(taskGateway, habitGateway, presenter);

        interactor.getFormattedTasksAndHabits(inputData);

        ViewTasksAndHabitsState resultState = testViewModel.getState();
        assertNotNull(resultState);

        assertEquals("Failed to load task and habit data", resultState.getErrorMessage());
    }

    private static class TestPresenter extends ViewTasksAndHabitsPresenter {

        private final ViewTasksAndHabitsViewModel vm;

        TestPresenter(ViewManagerModel viewManagerModel, ViewTasksAndHabitsViewModel vm) {
            super(viewManagerModel, vm);
            this.vm = vm;
        }

        @Override
        public void prepareSuccessView(ViewTasksAndHabitsOutputData data) {
            ViewTasksAndHabitsState state = new ViewTasksAndHabitsState();
            state.setFormattedTasks(data.getFormattedTasks());
            state.setFormattedHabits(data.getFormattedHabits());
            vm.setState(state);
        }

        @Override
        public void prepareFailView(String errorMessage) {
            ViewTasksAndHabitsState state = new ViewTasksAndHabitsState();
            state.setErrorMessage(errorMessage);
            vm.setState(state);
        }
    }
}


