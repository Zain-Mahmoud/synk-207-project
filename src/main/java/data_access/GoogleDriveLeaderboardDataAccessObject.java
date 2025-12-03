package data_access;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import entities.Habit;
import entities.HabitBuilder;
import use_case.gateways.LeaderboardGateway;

/**
 * Google Drive-backed read-only implementation of {@link LeaderboardGateway}.
 * This reads habits.csv from Google Drive for online leaderboard display.
 * 
 * Note: This is READ-ONLY. All habit CRUD operations (create, modify, delete)
 * continue using local CSV storage (HabitDataAccessObject).
 */
public class GoogleDriveLeaderboardDataAccessObject implements LeaderboardGateway {

    private static final String APPLICATION_NAME = "CSC-207-SYNK";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String DEFAULT_USER_ID = "user";
    private static final Duration TOKEN_VALIDITY = Duration.ofDays(30);
    private static final String TOKEN_METADATA_SUFFIX = "-leaderboard-auth.meta";
    private static final String HABIT_HEADER = "username,habitName,streakCount,startDateTime,frequency,habitGroup,priority,status";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final NetHttpTransport httpTransport;
    private final GoogleAuthorizationCodeFlow authorizationFlow;
    private final String defaultUserId;
    private final String driveFileId;
    private final Map<String, Drive> serviceCache = new HashMap<>();

    /**
     * Creates a Drive-backed leaderboard DAO with the specified Drive file ID.
     *
     * @param driveFileId the Google Drive file ID of the habits.csv file
     * @throws IOException              if the credential file cannot be read
     * @throws GeneralSecurityException if the HTTP transport cannot be trusted
     */
    public GoogleDriveLeaderboardDataAccessObject(String driveFileId) throws IOException, GeneralSecurityException {
        this(DEFAULT_USER_ID, driveFileId);
    }

    /**
     * Creates a Drive-backed leaderboard DAO with a specific user ID and Drive file ID.
     *
     * @param userId     identifier used to store OAuth tokens
     * @param driveFileId the Google Drive file ID of the habits.csv file
     * @throws IOException              if the credential file cannot be read
     * @throws GeneralSecurityException if the HTTP transport cannot be trusted
     */
    public GoogleDriveLeaderboardDataAccessObject(String userId, String driveFileId)
            throws IOException, GeneralSecurityException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.authorizationFlow = buildAuthorizationFlow(httpTransport);
        this.defaultUserId = normalizeUserId(userId);
        this.driveFileId = driveFileId;
    }

    private GoogleAuthorizationCodeFlow buildAuthorizationFlow(NetHttpTransport httpTransport) throws IOException {
        InputStream credentialsStream = GoogleDriveLeaderboardDataAccessObject.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(metaFile, false))) {
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

    private synchronized Drive getServiceForUser(String userId) throws IOException {
        final String normalizedUserId = normalizeUserId(userId);
        Drive existing = serviceCache.get(normalizedUserId);
        if (existing != null) {
            return existing;
        }
        Credential credential = ensureCredential(normalizedUserId);
        Drive service = new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        serviceCache.put(normalizedUserId, service);
        return service;
    }

    /**
     * Downloads the CSV file from Google Drive and parses it.
     */
    private Map<String, List<Habit>> loadHabitsFromDrive() throws IOException {
        Drive service = getServiceForUser(defaultUserId);
        Map<String, List<Habit>> userHabits = new HashMap<>();
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            service.files().get(driveFileId).executeMediaAndDownloadTo(outputStream);
            byte[] fileContent = outputStream.toByteArray();
            
            // Parse CSV content
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new java.io.ByteArrayInputStream(fileContent)))) {
                final String headerLine = reader.readLine();
                if (headerLine == null || !HABIT_HEADER.equals(headerLine)) {
                    return userHabits; // Empty or invalid header
                }
                
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    final String[] columns = line.split(",", -1);
                    if (columns.length < 8) {
                        continue;
                    }

                    final String username = columns[0].trim();
                    final String habitName = columns[1].trim();
                    final String startDateTimeRaw = columns[3].trim();
                    final String frequencyRaw = columns[4].trim();
                    final String habitGroup = columns[5].trim();

                    int streakCount;
                    int priority;
                    boolean status;

                    try {
                        streakCount = columns[2].trim().isBlank() ? 0 : Integer.parseInt(columns[2].trim());
                        priority = columns[6].trim().isBlank() ? 0 : Integer.parseInt(columns[6].trim());
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    try {
                        status = Boolean.parseBoolean(columns[7].trim());
                    } catch (Exception e) {
                        continue;
                    }

                    LocalDateTime startDateTime = null;
                    if (!startDateTimeRaw.isBlank()) {
                        try {
                            startDateTime = LocalDateTime.parse(startDateTimeRaw, DATE_FORMATTER);
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    int frequency = 0;
                    if (!frequencyRaw.isBlank()) {
                        try {
                            frequency = Integer.parseInt(frequencyRaw);
                        } catch (NumberFormatException e) {
                            continue;
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
                }
            }
        }
        
        return userHabits;
    }

    @Override
    public Map<String, List<Habit>> getAllUsersWithHabits() {
        try {
            return loadHabitsFromDrive();
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            // Provide more specific error messages
            if (e.getStatusCode() == 403) {
                if (e.getMessage().contains("SERVICE_DISABLED") || e.getMessage().contains("accessNotConfigured")) {
                    throw new RuntimeException(
                        "Google Drive API is not enabled. Please enable it at: " +
                        "https://console.cloud.google.com/apis/library/drive.googleapis.com", e);
                } else if (e.getMessage().contains("insufficientPermissions")) {
                    throw new RuntimeException(
                        "Insufficient permissions. Please delete the 'tokens' folder and re-authorize.", e);
                } else {
                    throw new RuntimeException("Access denied. Check file permissions in Google Drive.", e);
                }
            } else if (e.getStatusCode() == 404) {
                throw new RuntimeException("File not found in Google Drive. Check the file ID.", e);
            } else {
                throw new RuntimeException("Google Drive API error: " + e.getMessage(), e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                "credentials.json not found. Please place it in src/main/resources/ directory.", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load habits from Google Drive: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the file ID from a Google Drive shareable link.
     * Supports multiple URL formats:
     * - https://drive.google.com/file/d/{FILE_ID}/view
     * - https://drive.google.com/open?id={FILE_ID}
     * - https://docs.google.com/document/d/{FILE_ID}/edit
     * 
     * @param driveUrl the Google Drive file URL
     * @return the extracted file ID, or null if not found
     */
    public static String extractFileIdFromUrl(String driveUrl) {
        if (driveUrl == null || driveUrl.trim().isEmpty()) {
            return null;
        }
        
        // Pattern 1: /file/d/{FILE_ID}/view or /file/d/{FILE_ID}/edit
        Pattern pattern1 = Pattern.compile("/file/d/([a-zA-Z0-9_-]+)");
        Matcher matcher1 = pattern1.matcher(driveUrl);
        if (matcher1.find()) {
            return matcher1.group(1);
        }
        
        // Pattern 2: /open?id={FILE_ID}
        Pattern pattern2 = Pattern.compile("[?&]id=([a-zA-Z0-9_-]+)");
        Matcher matcher2 = pattern2.matcher(driveUrl);
        if (matcher2.find()) {
            return matcher2.group(1);
        }
        
        // Pattern 3: /document/d/{FILE_ID}/ or /spreadsheet/d/{FILE_ID}/
        Pattern pattern3 = Pattern.compile("/[a-z]+/d/([a-zA-Z0-9_-]+)");
        Matcher matcher3 = pattern3.matcher(driveUrl);
        if (matcher3.find()) {
            return matcher3.group(1);
        }
        
        // If no pattern matches, assume the input is already a file ID
        if (driveUrl.matches("[a-zA-Z0-9_-]+")) {
            return driveUrl;
        }
        
        return null;
    }
}

