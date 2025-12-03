package data_access;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import entities.Habit;
import entities.HabitBuilder;
import use_case.gateways.HabitGateway;

/**
 * Google Sheets-backed implementation of {@link HabitGateway} that stores habits
 * in a shared Google Sheet, enabling multi-user online leaderboard functionality.
 * 
 * This implementation reuses the OAuth authentication architecture from
 * {@link GoogleCalendarDataAccessObject} and requires Google Sheets API access.
 */
public class GoogleSheetsHabitDataAccessObject implements HabitGateway {

    private static final String APPLICATION_NAME = "CSC-207-SYNK";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Arrays.asList(
            SheetsScopes.SPREADSHEETS,
            SheetsScopes.DRIVE_FILE
    );
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String DEFAULT_USER_ID = "user";
    private static final Duration TOKEN_VALIDITY = Duration.ofDays(30);
    private static final String TOKEN_METADATA_SUFFIX = "-sheets-auth.meta";
    private static final String HABIT_HEADER = "uid,username,habitName,streakCount,startDateTime,frequency,habitGroup,priority,status";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String DEFAULT_SHEET_NAME = "Sheet1";
    private static final String DEFAULT_SPREADSHEET_ID = ""; // Must be set via constructor or setter

    private final NetHttpTransport httpTransport;
    private final GoogleAuthorizationCodeFlow authorizationFlow;
    private final String defaultUserId;
    private final Map<String, Sheets> serviceCache = new HashMap<>();
    private String spreadsheetId;
    private String sheetName;

    /**
     * Creates a Google Sheets DAO with default spreadsheet ID.
     * The spreadsheet ID must be set before use via {@link #setSpreadsheetId(String)}.
     *
     * @throws IOException              if the credential file cannot be read
     * @throws GeneralSecurityException if the HTTP transport cannot be trusted
     */
    public GoogleSheetsHabitDataAccessObject() throws IOException, GeneralSecurityException {
        this(DEFAULT_USER_ID, null, DEFAULT_SHEET_NAME);
    }

    /**
     * Creates a Google Sheets DAO scoped to a specific spreadsheet.
     *
     * @param spreadsheetId the Google Sheet ID (extracted from the Sheet URL)
     * @throws IOException              if the credential file cannot be read
     * @throws GeneralSecurityException if the HTTP transport cannot be trusted
     */
    public GoogleSheetsHabitDataAccessObject(String spreadsheetId) throws IOException, GeneralSecurityException {
        this(DEFAULT_USER_ID, spreadsheetId, DEFAULT_SHEET_NAME);
    }

    /**
     * Creates a Google Sheets DAO with custom user ID and spreadsheet.
     *
     * @param userId        identifier used to store OAuth tokens (typically username for authentication)
     * @param spreadsheetId the Google Sheet ID
     * @param sheetName     the name of the sheet tab (e.g., "Sheet1")
     * @throws IOException              if the credential file cannot be read
     * @throws GeneralSecurityException if the HTTP transport cannot be trusted
     */
    public GoogleSheetsHabitDataAccessObject(String userId, String spreadsheetId, String sheetName)
            throws IOException, GeneralSecurityException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.authorizationFlow = buildAuthorizationFlow(httpTransport);
        this.defaultUserId = normalizeUserId(userId);
        this.spreadsheetId = spreadsheetId;
        this.sheetName = sheetName != null ? sheetName : DEFAULT_SHEET_NAME;
    }

    /**
     * Sets the Google Spreadsheet ID. This can be extracted from the Sheet URL:
     * https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit
     *
     * @param spreadsheetId the Google Sheet ID
     */
    public void setSpreadsheetId(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

    /**
     * Sets the sheet name (tab name) within the spreadsheet.
     *
     * @param sheetName the name of the sheet tab
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName != null ? sheetName : DEFAULT_SHEET_NAME;
    }

    /* -----------------------------------------------------------------------
       AUTHENTICATION HELPERS (Reused from GoogleCalendarDataAccessObject)
       ----------------------------------------------------------------------- */

    private GoogleAuthorizationCodeFlow buildAuthorizationFlow(NetHttpTransport httpTransport) throws IOException {
        InputStream credentialsStream = GoogleSheetsHabitDataAccessObject.class
                .getResourceAsStream(CREDENTIALS_FILE_PATH);
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
            // Ignore timestamp write failures
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
                return false;
            }
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
        return expiresInSeconds != null && expiresInSeconds <= 300;
    }

    private Credential ensureCredential(String normalizedUserId) throws IOException {
        Credential credential = authorizationFlow.loadCredential(normalizedUserId);
        final boolean missingOrStale = !isCredentialFresh(normalizedUserId, credential);
        if (missingOrStale) {
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            AuthorizationCodeInstalledApp installer = new AuthorizationCodeInstalledApp(authorizationFlow, receiver);
            credential = installer.authorize(normalizedUserId);
            recordAuthorizationTimestamp(normalizedUserId);
            serviceCache.remove(normalizedUserId);
        } else if (shouldRefreshSoon(credential) && credential.refreshToken()) {
            recordAuthorizationTimestamp(normalizedUserId);
        }
        return credential;
    }

    private synchronized Sheets getServiceForUser(String userId) throws IOException {
        final String normalizedUserId = normalizeUserId(userId);
        Sheets existing = serviceCache.get(normalizedUserId);
        if (existing != null) {
            return existing;
        }
        Credential credential = ensureCredential(normalizedUserId);
        Sheets service = new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        serviceCache.put(normalizedUserId, service);
        return service;
    }

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

    private void ensureSpreadsheetId() {
        if (spreadsheetId == null || spreadsheetId.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Spreadsheet ID not set. Use setSpreadsheetId() or constructor with spreadsheetId parameter.");
        }
    }

    /* -----------------------------------------------------------------------
       GOOGLE SHEETS OPERATIONS
       ----------------------------------------------------------------------- */

    /**
     * Reads all data from the Google Sheet.
     *
     * @return list of rows, where each row is a list of cell values
     */
    private List<List<Object>> readAllFromSheet(String userId) throws IOException {
        ensureSpreadsheetId();
        Sheets service = getServiceForUser(userId);
        String range = sheetName + "!A:I"; // Read columns A through I (added uid column)
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        return values != null ? values : new ArrayList<>();
    }

    /**
     * Writes all data to the Google Sheet, replacing existing content.
     *
     * @param rows list of rows to write
     */
    private void writeAllToSheet(String userId, List<List<Object>> rows) throws IOException {
        ensureSpreadsheetId();
        Sheets service = getServiceForUser(userId);
        String range = sheetName + "!A1";
        ValueRange body = new ValueRange().setValues(rows);
        service.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    /**
     * Initializes the sheet with header row if it doesn't exist.
     */
    private void initializeSheetIfNeeded(String userId) throws IOException {
        List<List<Object>> existingData = readAllFromSheet(userId);
        if (existingData.isEmpty()) {
            List<Object> headerRow = Arrays.asList((Object[]) HABIT_HEADER.split(","));
            List<List<Object>> header = Collections.singletonList(headerRow);
            writeAllToSheet(userId, header);
        }
    }

    /**
     * Converts a list of rows from Google Sheets to internal data structure.
     * Supports both old format (without uid) and new format (with uid).
     */
    private Map<String, ArrayList<Habit>> parseRowsToHabits(List<List<Object>> rows) {
        Map<String, ArrayList<Habit>> userHabits = new HashMap<>();
        if (rows.isEmpty()) {
            return userHabits;
        }

        // Skip header row
        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            if (row.size() < 8) {
                continue;
            }

            try {
                // Support both old format (8 columns) and new format (9 columns with uid)
                int offset = row.size() >= 9 ? 1 : 0; // If 9 columns, skip uid column
                // uid is stored but not currently used in parsing (reserved for future user tracking)
                String username = safeString(row.get(0 + offset));
                String habitName = safeString(row.get(1 + offset));
                int streakCount = safeInt(row.get(2 + offset), 0);
                String startDateTimeRaw = safeString(row.get(3 + offset));
                int frequency = safeInt(row.get(4 + offset), 0);
                String habitGroup = safeString(row.get(5 + offset));
                int priority = safeInt(row.get(6 + offset), 0);
                boolean status = safeBoolean(row.get(7 + offset), false);

                LocalDateTime startDateTime = null;
                if (!startDateTimeRaw.isBlank()) {
                    try {
                        startDateTime = LocalDateTime.parse(startDateTimeRaw, DATE_FORMATTER);
                    } catch (Exception e) {
                        // Skip invalid date
                    }
                }

                Habit habit = new HabitBuilder()
                        .setHabitName(habitName)
                        .setStartDateTime(startDateTime)
                        .setFrequency(frequency)
                        .setHabitGroup(habitGroup)
                        .setStreakCount(streakCount)
                        .setPriority(priority)
                        .setStatus(status)
                        .build();

                userHabits.computeIfAbsent(username, key -> new ArrayList<>()).add(habit);
            } catch (Exception e) {
                // Skip invalid rows
                continue;
            }
        }
        return userHabits;
    }

    /**
     * Converts internal data structure to rows for Google Sheets.
     * Stores both uid and username for better user tracking.
     */
    private List<List<Object>> habitsToRows(Map<String, ArrayList<Habit>> userHabits, Map<String, String> uidMap) {
        List<List<Object>> rows = new ArrayList<>();
        // Add header
        rows.add(Arrays.asList((Object[]) HABIT_HEADER.split(",")));

        for (Map.Entry<String, ArrayList<Habit>> entry : userHabits.entrySet()) {
            String username = entry.getKey();
            String uid = uidMap.getOrDefault(username, ""); // Get uid if available, empty string otherwise
            for (Habit habit : entry.getValue()) {
                String startDateTime = habit.getStartTime() == null ? ""
                        : DATE_FORMATTER.format(habit.getStartTime());
                List<Object> row = Arrays.asList(
                        uid, // uid column
                        username,
                        safe(habit.getName()),
                        Integer.toString(habit.getStreakCount()),
                        startDateTime,
                        Integer.toString(habit.getFrequency()),
                        safe(habit.getHabitGroup()),
                        Integer.toString(habit.getPriority()),
                        Boolean.toString(habit.getStatus())
                );
                rows.add(row);
            }
        }
        return rows;
    }

    /**
     * Overloaded method for backward compatibility (without uid mapping).
     */
    private List<List<Object>> habitsToRows(Map<String, ArrayList<Habit>> userHabits) {
        return habitsToRows(userHabits, new HashMap<>());
    }

    private String safeString(Object value) {
        return value != null ? value.toString().trim() : "";
    }

    private int safeInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            String str = value.toString().trim();
            return str.isBlank() ? defaultValue : Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean safeBoolean(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value.toString().trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    /* -----------------------------------------------------------------------
       HABITGATEWAY INTERFACE IMPLEMENTATION
       ----------------------------------------------------------------------- */

    @Override
    public String addHabit(String userId, Habit habit) {
        try {
            ensureSpreadsheetId();
            initializeSheetIfNeeded(userId);
            List<List<Object>> rows = readAllFromSheet(userId);
            Map<String, ArrayList<Habit>> userHabits = parseRowsToHabits(rows);

            ArrayList<Habit> habitsForUser = userHabits.computeIfAbsent(userId, key -> new ArrayList<>());
            habitsForUser.add(habit);

            // Preserve uid mapping when writing
            Map<String, String> uidMap = extractUidMapping(rows);
            List<List<Object>> updatedRows = habitsToRows(userHabits, uidMap);
            writeAllToSheet(userId, updatedRows);
            return "Habit Added Successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Adding Habit: " + e.getMessage();
        }
    }

    @Override
    public ArrayList<Habit> fetchHabits(String userId) {
        try {
            ensureSpreadsheetId();
            List<List<Object>> rows = readAllFromSheet(userId);
            Map<String, ArrayList<Habit>> userHabits = parseRowsToHabits(rows);
            ArrayList<Habit> habits = userHabits.get(userId);
            return habits != null ? habits : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getAllUsernames() {
        try {
            ensureSpreadsheetId();
            List<List<Object>> rows = readAllFromSheet(defaultUserId);
            Map<String, ArrayList<Habit>> userHabits = parseRowsToHabits(rows);
            return new ArrayList<>(userHabits.keySet());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Habit> getHabitsForUser(String username) {
        try {
            ensureSpreadsheetId();
            List<List<Object>> rows = readAllFromSheet(defaultUserId);
            Map<String, ArrayList<Habit>> userHabits = parseRowsToHabits(rows);
            ArrayList<Habit> habits = userHabits.get(username);
            return habits != null ? new ArrayList<>(habits) : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, List<Habit>> getAllUsersWithHabits() {
        try {
            ensureSpreadsheetId();
            List<List<Object>> rows = readAllFromSheet(defaultUserId);
            Map<String, ArrayList<Habit>> userHabits = parseRowsToHabits(rows);
            Map<String, List<Habit>> result = new HashMap<>();
            for (Map.Entry<String, ArrayList<Habit>> entry : userHabits.entrySet()) {
                result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public boolean deleteHabit(String userId, Habit habit) {
        try {
            ensureSpreadsheetId();
            List<List<Object>> rows = readAllFromSheet(userId);
            Map<String, ArrayList<Habit>> userHabits = parseRowsToHabits(rows);

            // Use username + habitName for reliable deletion instead of object equality
            // This is safer because objects are rebuilt from the sheet and may not match by reference
            ArrayList<Habit> habits = userHabits.get(userId);
            if (habits == null) {
                return false;
            }

            // Find and remove by habitName (more reliable than object equality)
            boolean removed = false;
            String targetHabitName = habit.getName();
            for (int i = habits.size() - 1; i >= 0; i--) {
                if (habits.get(i).getName().equals(targetHabitName)) {
                    habits.remove(i);
                    removed = true;
                    break; // Remove only the first match
                }
            }

            if (removed) {
                // Rebuild uid mapping from existing rows for persistence
                Map<String, String> uidMap = extractUidMapping(rows);
                List<List<Object>> updatedRows = habitsToRows(userHabits, uidMap);
                writeAllToSheet(userId, updatedRows);
            }
            return removed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extracts uid to username mapping from existing sheet rows.
     */
    private Map<String, String> extractUidMapping(List<List<Object>> rows) {
        Map<String, String> uidMap = new HashMap<>();
        if (rows.isEmpty()) {
            return uidMap;
        }

        // Check if header has uid column (new format)
        List<Object> header = rows.get(0);
        boolean hasUidColumn = header.size() >= 9 && "uid".equals(safeString(header.get(0)));

        if (hasUidColumn) {
            for (int i = 1; i < rows.size(); i++) {
                List<Object> row = rows.get(i);
                if (row.size() >= 9) {
                    String uid = safeString(row.get(0));
                    String username = safeString(row.get(1));
                    if (!uid.isEmpty() && !username.isEmpty()) {
                        uidMap.put(username, uid);
                    }
                }
            }
        }
        return uidMap;
    }
}

