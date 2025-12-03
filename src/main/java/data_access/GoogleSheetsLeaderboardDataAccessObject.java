package data_access;

import java.io.File;
import java.io.FileNotFoundException;
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
import use_case.gateways.LeaderboardGateway;

/**
 * Google Sheets-backed implementation of {@link LeaderboardGateway} for online leaderboard.
 * This is a read-only data access object that only retrieves data for leaderboard display.
 * 
 * Other features (create, modify, delete habits) continue using local CSV storage.
 */
public class GoogleSheetsLeaderboardDataAccessObject implements LeaderboardGateway {

    private static final String APPLICATION_NAME = "CSC-207-SYNK";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Arrays.asList(
            SheetsScopes.SPREADSHEETS_READONLY,
            SheetsScopes.DRIVE_READONLY
    );
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String DEFAULT_USER_ID = "user";
    private static final Duration TOKEN_VALIDITY = Duration.ofDays(30);
    private static final String TOKEN_METADATA_SUFFIX = "-leaderboard-auth.meta";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String DEFAULT_SHEET_NAME = "Sheet1";

    private final NetHttpTransport httpTransport;
    private final GoogleAuthorizationCodeFlow authorizationFlow;
    private final String defaultUserId;
    private final Map<String, Sheets> serviceCache = new HashMap<>();
    private final String spreadsheetId;
    private final String sheetName;

    /**
     * Creates a Google Sheets leaderboard DAO.
     *
     * @param spreadsheetId the Google Sheet ID (extracted from the Sheet URL)
     * @throws IOException              if the credential file cannot be read
     * @throws GeneralSecurityException if the HTTP transport cannot be trusted
     */
    public GoogleSheetsLeaderboardDataAccessObject(String spreadsheetId) 
            throws IOException, GeneralSecurityException {
        this(DEFAULT_USER_ID, spreadsheetId, DEFAULT_SHEET_NAME);
    }

    /**
     * Creates a Google Sheets leaderboard DAO with custom user ID and sheet name.
     *
     * @param userId        identifier used to store OAuth tokens
     * @param spreadsheetId the Google Sheet ID
     * @param sheetName     the name of the sheet tab (e.g., "Sheet1")
     * @throws IOException              if the credential file cannot be read
     * @throws GeneralSecurityException if the HTTP transport cannot be trusted
     */
    public GoogleSheetsLeaderboardDataAccessObject(String userId, String spreadsheetId, String sheetName)
            throws IOException, GeneralSecurityException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.authorizationFlow = buildAuthorizationFlow(httpTransport);
        this.defaultUserId = normalizeUserId(userId);
        this.spreadsheetId = spreadsheetId;
        this.sheetName = sheetName != null ? sheetName : DEFAULT_SHEET_NAME;
    }

    private GoogleAuthorizationCodeFlow buildAuthorizationFlow(NetHttpTransport httpTransport) throws IOException {
        InputStream credentialsStream = GoogleSheetsLeaderboardDataAccessObject.class
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
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter(metaFile, false))) {
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

    /**
     * Reads all data from the Google Sheet.
     */
    private List<List<Object>> readAllFromSheet(String userId) throws IOException {
        Sheets service = getServiceForUser(userId);
        String range = sheetName + "!A:I"; // Read columns A through I
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        return values != null ? values : new ArrayList<>();
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

        // Check header to determine format
        List<Object> headerRow = rows.get(0);
        boolean hasUidColumn = headerRow.size() >= 9 && "uid".equals(safeString(headerRow.get(0)));

        // Parse data rows (skip header row)
        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            
            // Determine offset based on row size
            int offset = 0;
            if (row.size() < 8) {
                continue;
            } else if (row.size() >= 9 && hasUidColumn) {
                offset = 1;
            } else if (row.size() >= 8 && !hasUidColumn) {
                offset = 0;
            } else if (row.size() >= 9 && !hasUidColumn) {
                String firstCol = safeString(row.get(0));
                if (firstCol.length() > 20 || firstCol.contains("-")) {
                    offset = 1;
                } else {
                    offset = 0;
                }
            } else if (row.size() == 8 && hasUidColumn) {
                offset = 0;
            } else {
                offset = 0;
            }

            try {
                if (row.size() < (8 + offset)) {
                    continue;
                }

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

    @Override
    public Map<String, List<Habit>> getAllUsersWithHabits() {
        try {
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
}

