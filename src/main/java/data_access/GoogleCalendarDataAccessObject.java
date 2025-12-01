package data_access;// package data_access;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import entities.Task;
import use_case.gateways.CalendarGateway;


public class GoogleCalendarDataAccessObject implements CalendarGateway {

    private static final String APPLICATION_NAME = "CSC-207-SYNK";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String TIME_ZONE_ID = "America/Toronto";
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR); // Changed to Write access
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String DEFAULT_USER_ID = "user";
    private static final Duration TOKEN_VALIDITY = Duration.ofDays(30); // Re-authorize after 30 days
    private static final String TOKEN_METADATA_SUFFIX = "-auth.meta";

    private final NetHttpTransport httpTransport;
    private final GoogleAuthorizationCodeFlow authorizationFlow;
    private final String defaultUserId;
    private final Map<String, Calendar> serviceCache = new HashMap<>(); // Calendar clients keyed by userId

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
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.authorizationFlow = buildAuthorizationFlow(httpTransport);
        this.defaultUserId = normalizeUserId(userId);
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

    private File metadataFile(String normalizedUserId) {
        return new File(TOKENS_DIRECTORY_PATH, normalizedUserId + TOKEN_METADATA_SUFFIX);
    }

    private void recordAuthorizationTimestamp(String normalizedUserId) {
        final File metaFile = metadataFile(normalizedUserId);
        if (metaFile.getParentFile() != null) {
            metaFile.getParentFile().mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaFile, false))) {
            writer.write(Long.toString(System.currentTimeMillis()));
        } catch (IOException ignored) {
            // Ignore timestamp write failures; the credential itself is still usable.
        }
    }

    private boolean isCredentialFresh(String normalizedUserId, Credential credential) {
        if (credential == null) {
            return false;
        }
        final File metaFile = metadataFile(normalizedUserId);
        if (!metaFile.exists()) {
            return false;
        }
        try {
            final String raw = Files.readString(metaFile.toPath()).trim();
            if (raw.isEmpty()) {
                return false;
            }
            final long issuedAt = Long.parseLong(raw);
            final long ageMillis = System.currentTimeMillis() - issuedAt;
            if (ageMillis > TOKEN_VALIDITY.toMillis()) {
                return false; // past the 30-day window
            }
            // If no refresh token is present, ensure the access token is still valid.
            if (credential.getRefreshToken() == null) {
                final Long expiresAt = credential.getExpirationTimeMilliseconds();
                return expiresAt != null && expiresAt > System.currentTimeMillis();
            }
            return true;
        } catch (IOException | NumberFormatException e) {
            return false;
        }
    }

    private boolean shouldRefreshSoon(Credential credential) {
        final Long expiresInSeconds = credential.getExpiresInSeconds();
        return expiresInSeconds != null && expiresInSeconds <= 300; // refresh if expiring in <= 5 minutes
    }

    private Credential ensureCredential(String normalizedUserId) throws IOException {
        Credential credential = authorizationFlow.loadCredential(normalizedUserId);
        final boolean missingOrStale = !isCredentialFresh(normalizedUserId, credential);
        if (missingOrStale) {
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            AuthorizationCodeInstalledApp installer = new AuthorizationCodeInstalledApp(authorizationFlow, receiver);
            credential = installer.authorize(normalizedUserId);
            recordAuthorizationTimestamp(normalizedUserId);
            serviceCache.remove(normalizedUserId); // Drop any cached client bound to an old token
        } else if (shouldRefreshSoon(credential) && credential.refreshToken()) {
            recordAuthorizationTimestamp(normalizedUserId);
        }
        return credential;
    }

    private synchronized Calendar getServiceForUser(String userId) throws IOException {
        final String normalizedUserId = normalizeUserId(userId);
        Calendar existing = serviceCache.get(normalizedUserId);
        if (existing != null) {
            return existing;
        }
        Credential credential = ensureCredential(normalizedUserId);
        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        serviceCache.put(normalizedUserId, service);
        return service;
    }

    // Standardize the USER ID STRING to access later
    private String normalizeUserId(String userId) {
        if (userId == null) {
            return defaultUserId == null ? DEFAULT_USER_ID : defaultUserId;
        }
        String trimmed = userId.trim();
        if (trimmed.isEmpty()) {
            return defaultUserId == null ? DEFAULT_USER_ID : defaultUserId;
        }
        return trimmed;
    }

    private String resolveCalendarId(String userId) {
        // Default to the user's primary calendar unless a full email calendar ID is provided.
        if (userId != null) {
            String trimmed = userId.trim();
            if (!trimmed.isEmpty() && trimmed.contains("@")) { // treat probable email as calendar id
                return trimmed;
            }
        }
        return "primary";
    }

    /**
     * Public helper that attempts to load a previously stored credential without prompting.
     */
    public Credential getStoredCredential(String userId) throws IOException {
        String normalizedUserId = normalizeUserId(userId);
        Credential credential = authorizationFlow.loadCredential(normalizedUserId);
        if (!isCredentialFresh(normalizedUserId, credential)) {
            return null;
        }
        return credential; // fetch credential without prompting
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
            Calendar service = getServiceForUser(userId);
            Event event = new Event()
                    .setSummary(task.getName()) // Assuming Task has getName()
                    .setDescription(task.getDescription())
                    ;

            // Derive start/end from the task (fallback to now/+1h if absent)
            LocalDateTime startTime = null;
            if (task instanceof Task) {
                startTime = ((Task) task).getStartTime();
            }
            if (startTime == null) {
                startTime = LocalDateTime.now();
            }

            LocalDateTime endTime = task.getDueDate();
            if (endTime == null) {
                endTime = startTime.plusHours(1);
            }

            DateTime startDateTime = toGoogleDateTime(startTime);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(TIME_ZONE_ID); // Hardcoded for now, ideally passed in
            event.setStart(start);

            DateTime endDateTime = toGoogleDateTime(endTime);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone(TIME_ZONE_ID);
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
            Calendar service = getServiceForUser(userId);
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
            Calendar service = getServiceForUser(userId);
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
            Calendar service = getServiceForUser(userId);
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
            return getStoredCredential(userId) != null; // true when credential exists and is fresh
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

    private DateTime toGoogleDateTime(LocalDateTime dateTime) {
        return new DateTime(dateTime.atZone(ZoneId.of(TIME_ZONE_ID)).toInstant().toEpochMilli());
    }

}
