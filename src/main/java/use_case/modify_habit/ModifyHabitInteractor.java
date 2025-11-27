package use_case.modify_habit;

import entities.Habit;
import entities.HabitBuilder;
import use_case.gateways.HabitGateway;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ModifyHabitInteractor implements ModifyHabitInputBoundary {
    private final ModifyHabitOutputBoundary modifyHabitPresenter;
    private final HabitGateway userDataAccessObject;

    public ModifyHabitInteractor(ModifyHabitOutputBoundary modifyHabitPresenter, HabitGateway userDataAccessObject) {
        this.modifyHabitPresenter = modifyHabitPresenter;
        this.userDataAccessObject = userDataAccessObject;
    }

    public void execute(ModifyHabitInputData modifyHabitInputData) {
        String userID = modifyHabitInputData.getUserID();
        

        String oldHabitName = modifyHabitInputData.getOldHabitName();
        String oldPriority = modifyHabitInputData.getOldPriority();
        boolean oldHabitStatus = modifyHabitInputData.getOldHabitStatus();
        String oldStartDateTime = modifyHabitInputData.getOldStartDateTime();
        String oldStreakCount = modifyHabitInputData.getOldStreakCount();
        String oldHabitGroup = modifyHabitInputData.getOldHabitGroup();
        String oldFrequency = modifyHabitInputData.getOldFrequency();
        

        String newHabitName = modifyHabitInputData.getNewHabitName();
        String newPriority = modifyHabitInputData.getNewPriority();
        boolean newHabitStatus = modifyHabitInputData.getNewHabitStatus();
        String newStartDateTime = modifyHabitInputData.getNewStartDateTime();
        String newStreakCount = modifyHabitInputData.getNewStreakCount();
        String newHabitGroup = modifyHabitInputData.getNewHabitGroup();
        String newFrequency = modifyHabitInputData.getNewFrequency();

        try {

            LocalDateTime newStartDateTimeFormatted = LocalDateTime.parse(newStartDateTime);
            LocalDateTime newFrequencyFormatted = LocalDateTime.parse(newFrequency);
            int newPriorityFormatted = Integer.parseInt(newPriority);
            int newStreakCountFormatted = Integer.parseInt(newStreakCount);


            final Habit modifiedHabit = new HabitBuilder()
                    .setHabitName(newHabitName)
                    .setPriority(newPriorityFormatted)
                    .setStatus(newHabitStatus)
                    .setStartDateTime(newStartDateTimeFormatted)
                    .setStreakCount(newStreakCountFormatted)
                    .setHabitGroup(newHabitGroup)
                    .setFrequency(newFrequencyFormatted)
                    .build();


            final Habit oldHabit = new HabitBuilder()
                    .setHabitName(oldHabitName)
                    .setPriority(Integer.parseInt(oldPriority))
                    .setStatus(oldHabitStatus)
                    .setStartDateTime(LocalDateTime.parse(oldStartDateTime))
                    .setStreakCount(Integer.parseInt(oldStreakCount))
                    .setHabitGroup(oldHabitGroup)
                    .setFrequency(LocalDateTime.parse(oldFrequency))
                    .build();

            ArrayList<Habit> habitList = userDataAccessObject.fetchHabits(userID);
            for (Habit habit : habitList) {
                if (!habit.equals(oldHabit) && habit.getName().equals(modifiedHabit.getName())){
                    modifyHabitPresenter.prepareFailView("Habit already exists");
                    return;
                }
            }

            userDataAccessObject.deleteHabit(userID, oldHabit);
            userDataAccessObject.addHabit(userID, modifiedHabit);

            modifyHabitPresenter.prepareSuccessView(new
                    ModifyHabitOutputData(userDataAccessObject.fetchHabits(userID)));

        } catch (java.time.format.DateTimeParseException d) {
            modifyHabitPresenter.prepareFailView("Invalid Date/Time Format");
        } catch (NumberFormatException n) {
            modifyHabitPresenter.prepareFailView("Invalid Priority or Streak Count");
        }
    }

    @Override
    public void switchToHabitListView() {
        modifyHabitPresenter.switchToHabitListView();
    }
}

