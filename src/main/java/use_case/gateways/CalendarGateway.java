package use_case.gateways;

import entities.Task;

public interface CalendarGateway {
    String getCalendarById(String userID);

    String getCalendarByUsername(String username);

    String getCalendarByEmail(String email);

    /*
    Event Management Methods
     */

    /**
     * @return the event ID of the created event
     *
     */
    String createEvent(String userId, Task task);


    boolean deleteEvent(String userId, String eventID);

    //
    boolean updateEvent(String userId, String eventID, Task updated);
}