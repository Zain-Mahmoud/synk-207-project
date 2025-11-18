package use_case.gateways;

// CRUD GET (read), PUT (create and update), POST (create - if we don't have `id` or `uuid`), and DELETE (delete)

public interface CalendarGateway {


    public void getInstance();
    public void createEvent();
    public void updateEvent();
    public void deleteEvent();
    public void fetchEventsForDateRange();


}