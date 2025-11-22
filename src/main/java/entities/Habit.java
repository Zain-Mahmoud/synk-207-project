package entities;

public class Habit implements Completable {
    private String habitName;
    private String description;
    private boolean status;

    public Habit(String habitName, String description) {
        this.habitName = habitName;
        this.description = description;
        this.status = false; // Habits are incomplete by default
    }

    @Override
    public void complete() {
        this.status = true;
    }

    @Override
    public boolean isCompleted() {
        return status;
    }

    public String getName() {
        return habitName;
    }

    public String getDescription() {
        return description;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
