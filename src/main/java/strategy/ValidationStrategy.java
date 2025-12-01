package strategy;

public interface ValidationStrategy<T> {

    /**
     * STRATEGY PATTERN: Defines the interface for a validation algorithm.
     * Executes the validation logic for a modification operation.
     *
     * @param userID The ID of the user whose data is being modified.
     * @param oldEntity The original entity (Task, Habit, etc.).
     * @param modifiedEntity The entity with proposed changes.
     * @return null if validation passes, otherwise an error message string.
     */
    String validate(String userID, T oldEntity, T modifiedEntity);
}