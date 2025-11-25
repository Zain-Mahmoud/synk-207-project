package data_access;// package data_access;

// I commented because im using java 24 modules and these are not required to be imported in module-info.java
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import entities.Completable;
import use_case.gateways.CalendarGateway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;


public class GoogleCalendarDataAccessObject implements CalendarGateway {

    private static final String APPLICATION_NAME = "CSC-207-SYNK";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR); // Changed to Write access
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String DEFAULT_USER_ID = "user";

    private final Calendar service;
    private final GoogleAuthorizationCodeFlow authorizationFlow;
    private final String credentialUserId;

    /**
     * Constructor: Initializes the Google Calendar Service with a default user identifier.
     */
    public GoogleCalendarDataAccessObject() throws IOException, GeneralSecurityException {
        this(DEFAULT_USER_ID);
    }

    /**
     * Constructor: Initializes the Google Calendar Service for a specific user.
     */
    public GoogleCalendarDataAccessObject(String userId) throws IOException, GeneralSecurityException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.authorizationFlow = buildAuthorizationFlow(httpTransport);
        this.credentialUserId = normalizeUserId(userId);
        Credential credential = ensureCredential(this.credentialUserId);
        this.service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /* -----------------------------------------------------------------------
       AUTHENTICATION HELPERS
       ----------------------------------------------------------------------- */
    private GoogleAuthorizationCodeFlow buildAuthorizationFlow(NetHttpTransport httpTransport) throws IOException {
        InputStream credentialsStream = GoogleCalendarDataAccessObject.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (credentialsStream == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        try (InputStreamReader reader = new InputStreamReader(credentialsStream)) {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
            return new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
        }
    }

    private Credential ensureCredential(String normalizedUserId) throws IOException {
        Credential credential = authorizationFlow.loadCredential(normalizedUserId);
        if (credential == null) {
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            AuthorizationCodeInstalledApp installer = new AuthorizationCodeInstalledApp(authorizationFlow, receiver);
            credential = installer.authorize(normalizedUserId);
        }
        return credential;
    }
    // Standardize the USER ID STRING to access later
    private String normalizeUserId(String userId) {
        if (userId == null) {
            return DEFAULT_USER_ID;
        }
        String trimmed = userId.trim();
        if (trimmed.isEmpty()) {
            return DEFAULT_USER_ID;
        }
        return trimmed;
    }

    private String resolveCalendarId(String userId) {
        if (userId == null) {
            return "primary";
        }
        String trimmed = userId.trim();
        if (trimmed.isEmpty()) {
            return "primary";
        }
        return trimmed;
    }

    /**
     * Public helper that attempts to load a previously stored credential without prompting.
     */
    public Credential getStoredCredential(String userId) throws IOException {
        return authorizationFlow.loadCredential(normalizeUserId(userId)); // fetch credential without prompting
    }

    /* -----------------------------------------------------------------------
       INTERFACE IMPLEMENTATION (CRUD)
       ----------------------------------------------------------------------- */


    /**
     * Create an Event on Google Calendar based on a Task entity.
     * @param userId The Calendar ID (usually "primary" or the user's email).
     * @param task The internal Task entity.
     * @return The Google Event ID.
     */
    @Override
    public String createEvent(String userId, Completable task) {
        try {
            Event event = new Event()
                    .setSummary(task.getName()) // Assuming Task has getName()
                    .setDescription(task.getDescription())
                    ;

            // START TIME (Assuming Task has a startDate, otherwise defaulting to NOW for prototype)
            DateTime startDateTime = new DateTime(System.currentTimeMillis());
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("America/Toronto"); // Hardcoded for now, ideally passed in
            event.setStart(start);

            // END TIME (Defaulting to +1 hour for prototype)
            DateTime endDateTime = new DateTime(System.currentTimeMillis() + 3600000);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("America/Toronto");
            event.setEnd(end);

            // EXECUTE API CALL
            String calendarId = resolveCalendarId(userId);
            Event createdEvent = service.events().insert(calendarId, event).execute();

            return createdEvent.getId();

        } catch (IOException e) {
            e.printStackTrace(); // In production, log this or throw a custom DataAccess exception
            return null;
        }
    }


    /**
     * Update an existing Google Calendar Event.
     */
    @Override
    public boolean updateEvent(String userId, String eventID, Completable updatedTask) {
        try {
            String calendarId = resolveCalendarId(userId);

            // 1. Retrieve the existing event (Good practice to ensure it exists)
            Event event = service.events().get(calendarId, eventID).execute();

            // 2. Update fields based on the new Task object
            event.setSummary(updatedTask.getName());
            // event.setDescription(updatedTask.getDescription());

            // 3. Execute Update
            service.events().update(calendarId, eventID, event).execute();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete an event from Google Calendar.
     */
    @Override
    public boolean deleteEvent(String userId, String eventID) {
        try {
            String calendarId = resolveCalendarId(userId);

            service.events().delete(calendarId, eventID).execute();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* -----------------------------------------------------------------------
       ADDITIONAL METHODS (Required by Interface or Requested)
       ----------------------------------------------------------------------- */

    /**
     * Fetches a list of events.
     * Note: Your Interface had `getCalendarById` returning String, but you asked for "Get Events".
     * I have implemented a list retrieval here. You may need to update your Interface to return List<String> or List<Task>.
     */
    @Override
    public List<Event> getEvents(String userId) {
        try {
            String calendarId = resolveCalendarId(userId);
            DateTime now = new DateTime(System.currentTimeMillis());

            Events events = service.events().list(calendarId)
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            return events.getItems();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public boolean hasStoredCredential(String userId) { // report if credential is already available
        try {
            return getStoredCredential(userId) != null; // true when credential exists
        } catch (IOException e) {
            return false; // treat IO failure as missing credential
        }
    }

    @Override
    public void authenticateUser(String userId) { //  run OAuth flow when credential missing
        try {
            ensureCredential(normalizeUserId(userId)); // invoke credential setup
        } catch (IOException e) {
            throw new RuntimeException("Failed to authenticate user for calendar", e); // surface auth errors to caller
        }
    }

}

