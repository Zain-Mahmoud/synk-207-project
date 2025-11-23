package data_access;

import entities.Task;
import entities.TaskBuilder;
import use_case.gateways.TaskGateway;

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
 * Persistence layer for Tasks backed by the API.
 * Implements Create, Update, Remove, Read (Fetch) operations for tasks.
 */
public class TaskDataAccessObject implements TaskGateway {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String PLACEHOLDER_PASSWORD = "password"; // ASSUMPTION for API authentication
    private static final String API_URL_USER = "http://vm003.teach.cs.toronto.edu:20112/user?username=%s";
    private static final String API_URL_MODIFY = "http://vm003.teach.cs.toronto.edu:20112/modifyUserInfo";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse(CONTENT_TYPE_JSON);
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();


    public TaskDataAccessObject() {
    }


    private JSONObject taskToJson(Task task) {
        JSONObject json = new JSONObject();
        json.put("taskName", safe(task.getName()));
        json.put("description", safe(task.getDescription()));
        json.put("deadline", task.getDeadline() == null ? "" : DATE_FORMATTER.format(task.getDeadline()));
        json.put("taskGroup", safe(task.getTaskGroup()));
        json.put("status", task.getStatus());
        json.put("priority", task.getPriority());
        json.put("startTime", task.getStartTime() == null ? "" : DATE_FORMATTER.format(task.getStartTime()));
        return json;
    }

    private Task jsonToTask(JSONObject json) {
        String taskName = json.getString("taskName");
        String description = json.getString("description");
        String deadlineRaw = json.getString("deadline");
        String taskGroup = json.getString("taskGroup");
        boolean status = json.getBoolean("status");
        int priority = json.getInt("priority");
        String startTimeRaw = json.getString("startTime");

        LocalDateTime deadline = deadlineRaw.isBlank() ? null : LocalDateTime.parse(deadlineRaw, DATE_FORMATTER);
        LocalDateTime startTime = startTimeRaw.isBlank() ? null : LocalDateTime.parse(startTimeRaw, DATE_FORMATTER);

        return new TaskBuilder()
                .setTaskName(taskName)
                .setDescription(description)
                .setDeadline(deadline)
                .setTaskGroup(taskGroup)
                .setStatus(status)
                .setPriority(priority)
                .setStartTime(startTime)
                .build();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }


    /**
     * Retrieves the list of tasks for a user by calling the API and deserializing the JSON array.
     */
    private ArrayList<Task> loadTasksFromApi(String userId) {
        final Request request = new Request.Builder()
                .url(String.format(API_URL_USER, userId))
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .build();

        try (Response response = client.newCall(request).execute()) {
            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getInt("status_code") == 200) {
                final JSONObject userJSONObject = responseBody.getJSONObject("user");
                final JSONObject info = userJSONObject.getJSONObject("info");

                if (!info.has("tasks")) {
                    return new ArrayList<>();
                }

                JSONArray tasksJsonArray = info.getJSONArray("tasks");
                ArrayList<Task> tasks = new ArrayList<>();
                for (int i = 0; i < tasksJsonArray.length(); i++) {
                    tasks.add(jsonToTask(tasksJsonArray.getJSONObject(i)));
                }
                return tasks;
            } else {
                // Handle API error response
                throw new RuntimeException("API error loading tasks: " + responseBody.getString("message"));
            }
        } catch (IOException | JSONException ex) {
            throw new RuntimeException("Failed to load tasks from API", ex);
        }
    }

    /**
     * Persists the list of tasks to the API by serializing them into a JSON array.
     */
    private void persistTasksToApi(String userId, List<Task> tasks) {
        JSONArray tasksJsonArray = new JSONArray(tasks.stream().map(this::taskToJson).collect(Collectors.toList()));

        final JSONObject requestBody = new JSONObject();
        requestBody.put("username", userId);
        requestBody.put("password", PLACEHOLDER_PASSWORD);

        final JSONObject info = new JSONObject();
        info.put("tasks", tasksJsonArray);

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
                throw new RuntimeException("API error persisting tasks: " + responseBody.getString("message"));
            }
        } catch (IOException | JSONException ex) {
            throw new RuntimeException("Failed to persist tasks to API", ex);
        }
    }


    @Override
    public String addTask(String userId, Task task) {
        try {
            ArrayList<Task> tasks = loadTasksFromApi(userId);
            tasks.add(task);
            persistTasksToApi(userId, tasks);
            return "Task Added Successfully";
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "Error Adding Task: " + e.getMessage();
        }
    }

    @Override
    public ArrayList<Task> fetchTasks(String userId) {
        try {
            return loadTasksFromApi(userId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean deleteTask(String userId, Task task) {
        try {
            ArrayList<Task> tasks = loadTasksFromApi(userId);
            boolean removed = tasks.remove(task);
            if (removed) {
                persistTasksToApi(userId, tasks);
            }
            return removed;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }
}