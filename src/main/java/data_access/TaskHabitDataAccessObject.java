package data_access;
import use_case.gateways.TaskGateway;

/**
 * TaskHabitDataAccessObject (LOCALLY)
 *
 * Short description:
 * - Data access layer for persisting and retrieving Task and Habit entities .
 *
 * Responsibilities / contract:
 * - Inputs: domain entities like Task and Habit (or DTOs/IDs) for create,
 *   update, delete, and query operations.
 * - Outputs: persisted entities, collections of entities, or boolean/status
 *   indicators for write operations.
 * - Error modes: persistence failures, validation errors, or missing records.
 *   Callers should handle these by checking return values or catching
 *   exceptions depending on implementation.
 *
 * Notes:
 * - Typical methods: addTask/addHabit, updateTask/updateHabit,
 *   deleteTask/deleteHabit, findTasksByDate, and methods to manage streaks.
 */
public class TaskHabitDataAccessObject implements TaskGateway {
    
}
