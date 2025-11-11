package data_access;

/**
 * UserDataAccessObject (LOCALLY)
 *
 * Short description:
 * - Data access layer responsible for storing and retrieving user
 *   information (profiles, settings, and authentication tokens).
 *
 * Responsibilities / contract:
 * - Inputs: user entities or credentials for create/update/lookup operations.
 * - Outputs: User objects, authentication tokens, or boolean status for
 *   changes.
 * - Error modes: credential validation failures, missing users, or
 *   persistence errors. Callers should validate inputs and handle errors or
 *   null returns accordingly.
 *
 * Notes:
 * - This class should encapsulate user persistence details and expose simple
 *   methods such as createUser, getUserById, updateUser, and deleteUser.
 *  *** IMPORTANT*** 
 *  - This assumes that we have seperate logins for Google Calendar and User accounts.
 */
public class UserDataAccessObject implements UserGateway {
    
}
