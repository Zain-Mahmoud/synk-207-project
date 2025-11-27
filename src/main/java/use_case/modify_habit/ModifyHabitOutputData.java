package use_case.modify_habit;

import entities.Habit;


import java.util.ArrayList;

public class ModifyHabitOutputData {

    private final ArrayList<Habit> habitList;

    public ModifyHabitOutputData(ArrayList<Habit> habitList) {
        this.habitList = habitList;
    }

    public ArrayList<Habit> getHabitList() {
        return habitList;
    }
}
