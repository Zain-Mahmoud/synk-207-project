package data_access;

import entities.Habit;
import entities.HabitBuilder;
import use_case.gateways.HabitGateway;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Persistence layer for Habits backed by the API.
 * Implements Create, Update, Remove, Read (Fetch) operations for habits.
 */
public class HabitDataAccessObject implements HabitGateway {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String PLACEHOLDER_PASSWORD = "password"; // ASSUMPTION for API authentication
    private static final String API_URL_USER = "http://vm003.teach.cs.toronto.edu:20112/user?username=%s";
    private static final String API_URL_MODIFY = "http://vm003.teach.cs.toronto.edu:20112/modifyUserInfo";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse(CONTENT_TYPE_JSON);
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();


    public HabitDataAccessObject() {
        // No local file initialization needed anymore
    }



    private JSONObject habitToJson(Habit habit) {
        JSONObject json = new JSONObject();
        json.put("habitName", safe(habit.getName()));
        json.put("description", safe(habit.getDescription()));
        json.put("startDateTime", habit.getStartDateTime() == null ? "" : DATE_FORMATTER.format(habit.getStartDateTime()));
        json.put("frequency", habit.getFrequency() == null ? "" : DATE_FORMATTER.format(habit.getFrequency()));
        json.put("habitGroup", safe(habit.getHabitGroup()));
        json.put("streakCount", habit.getStreakCount());
        json.put("priority", habit.getPriority());
        json.put("status", habit.getStatus());
        return json;
    }

    private Habit jsonToHabit(JSONObject json) {
        String habitName = json.getString("habitName");
        String description = json.getString("description");
        String startDateTimeRaw = json.getString("startDateTime");
        String frequencyRaw = json.getString("frequency");
        String habitGroup = json.getString("habitGroup");
        int streakCount = json.getInt("streakCount");
        int priority = json.getInt("priority");
        boolean status = json.getBoolean("status");

        LocalDateTime startDateTime = startDateTimeRaw.isBlank() ? null : LocalDateTime.parse(startDateTimeRaw, DATE_FORMATTER);
        LocalDateTime frequency = frequencyRaw.isBlank() ? null : LocalDateTime.parse(frequencyRaw, DATE_FORMATTER);

        HabitBuilder builder = new HabitBuilder()
                .setHabitName(habitName)
                .setStartDateTime(startDateTime)
                .setFrequency(frequency)
                .setHabitGroup(habitGroup)
                .setStreakCount(streakCount)
                .setPriority(priority)
                .setStatus(status);
        builder.build().setDescription(description);
        return builder.build();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }



    /**
     * Retrieves the list of habits for a user by calling the API and deserializing the JSON array.
     */
    private ArrayList<Habit> loadHabitsFromApi(String userId) {
        final Request request = new Request.Builder()
                .url(String.format(API_URL_USER, userId))
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .build();

        try (Response response = client.newCall(request).execute()) {
            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getInt("status_code") == 200) {
                final JSONObject userJSONObject = responseBody.getJSONObject("user");
                final JSONObject info = userJSONObject.getJSONObject("info");

                if (!info.has("habits")) {
                    return new ArrayList<>();
                }

                JSONArray habitsJsonArray = info.getJSONArray("habits");
                ArrayList<Habit> habits = new ArrayList<>();
                for (int i = 0; i < habitsJsonArray.length(); i++) {
                    habits.add(jsonToHabit(habitsJsonArray.getJSONObject(i)));
                }
                return habits;
            } else {
                throw new RuntimeException("API error loading habits: " + responseBody.getString("message"));
            }
        } catch (IOException | JSONException ex) {
            throw new RuntimeException("Failed to load habits from API", ex);
        }
    }

    /**
     * Persists the list of habits to the API by serializing them into a JSON array.
     */
    private void persistHabitsToApi(String userId, List<Habit> habits) {
        JSONArray habitsJsonArray = new JSONArray(habits.stream().map(this::habitToJson).collect(Collectors.toList()));

        final JSONObject requestBody = new JSONObject();
        requestBody.put("username", userId);
        requestBody.put("password", PLACEHOLDER_PASSWORD);

        final JSONObject info = new JSONObject();
        info.put("habits", habitsJsonArray);

        requestBody.put("info", info);
        final RequestBody body = RequestBody.create(requestBody.toString(), MEDIA_TYPE_JSON);

        final Request request = new Request.Builder()
                .url(API_URL_MODIFY)
                .method("PUT", body)
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .build();

        try (Response response = client.newCall(request).execute()) {
            final JSONObject responseBody = new JSONObject(response.body().string());
            if (responseBody.getInt("status_code") != 200) {
                throw new RuntimeException("API error persisting habits: " + responseBody.getString("message"));
            }
        } catch (IOException | JSONException ex) {
            throw new RuntimeException("Failed to persist habits to API", ex);
        }
    }



    @Override
    public String addHabit(String userId, Habit habit) {
        try {
            ArrayList<Habit> habits = loadHabitsFromApi(userId);
            habits.add(habit);
            persistHabitsToApi(userId, habits);
            return "Habit Added Successfully";
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "Error Adding Habit: " + e.getMessage();
        }
    }

    @Override
    public ArrayList<Habit> fetchHabits(String userId) {
        try {
            return loadHabitsFromApi(userId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean deleteHabit(String userId, Habit habit) {
        try {
            ArrayList<Habit> habits = loadHabitsFromApi(userId);
            boolean removed = habits.remove(habit);
            if (removed) {
                persistHabitsToApi(userId, habits);
            }
            return removed;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }
}