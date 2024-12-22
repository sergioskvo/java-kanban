package controllers;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.StatusCodes;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpTaskManagerTest {

    private HttpTaskServer server;
    private Gson gson;

    @BeforeAll
    void setup() throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @BeforeEach
    void startServer() throws IOException {
        InMemoryTaskManager.taskId = 0;
        HttpTaskServer.taskManager = Managers.getDefault();
        HttpTaskServer.historyManager = Managers.getDefaultHistory();
        server = new HttpTaskServer();
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void testGetTasks() throws IOException {
        URL url = new URL("http://localhost:8080/tasks");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
        String responseBody = readResponseBody(connection);
        assertEquals("[]", responseBody); // Ожидаем пустой список
    }

    @Test
    void testPostTask() throws IOException {
        URL url = new URL("http://localhost:8080/tasks");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        // Создаём задачу и отправляем её в запросе
        Task task = new Task("Test Task", "Description");
        String jsonTask = gson.toJson(task);
        connection.getOutputStream().write(jsonTask.getBytes());
        int responseCode = connection.getResponseCode();
        assertEquals(201, responseCode);
        String responseBody = readResponseBody(connection);
        assertTrue(responseBody.contains("\"id\":")); // Проверяем, что ID задачи возвращён
    }

    @Test
    void testPostTaskWithConflictingStartTime() throws IOException {
        URL url = new URL("http://localhost:8080/tasks");
        // Постим первую задачу
        HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();
        connection1.setRequestMethod("POST");
        connection1.setDoOutput(true);
        connection1.setRequestProperty("Content-Type", "application/json");
        Task task1 = new Task("Test Task", "Description", null,
                StatusCodes.NEW, Duration.ofHours(4),
                LocalDateTime.of(2024, 12, 20, 12, 0, 0)); // Задаем startTime
        String jsonTask1 = gson.toJson(task1);
        connection1.getOutputStream().write(jsonTask1.getBytes());
        int responseCode1 = connection1.getResponseCode();
        assertEquals(201, responseCode1, "Failed to post the first task");
        String responseBody1 = readResponseBody(connection1);
        assertTrue(responseBody1.contains("\"id\":"), "First task ID not returned");
        // Постим вторую задачу с таким же startTime
        HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
        connection2.setRequestMethod("POST");
        connection2.setDoOutput(true);
        connection2.setRequestProperty("Content-Type", "application/json");
        Task task2 = new Task("Conflicting Task", "Conflicting Description", null,
                StatusCodes.NEW, Duration.ofHours(4),
                LocalDateTime.of(2024, 12, 20, 12, 0, 0)); // Тот же startTime
        String jsonTask2 = gson.toJson(task2);
        connection2.getOutputStream().write(jsonTask2.getBytes());
        int responseCode2 = connection2.getResponseCode();
        assertEquals(406, responseCode2, "Expected 406 for conflicting task start time");
    }

    @Test
    void testDeleteTask() throws IOException {
        // Сначала добавляем задачу
        Task task = new Task("Task to Delete", "Description");
        String taskId = addTaskAndGetId(task);
        // Удаляем задачу
        URL url = new URL("http://localhost:8080/tasks/" + taskId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
        String responseBody = readResponseBody(connection);
        assertEquals("{\"status\": \"Task deleted\"}", responseBody);
    }

    @Test
    void testGetTaskById() throws IOException {
        Task task = new Task("Task to Retrieve", "Description");
        String taskId = addTaskAndGetId(task);
        URL url = new URL("http://localhost:8080/tasks/" + taskId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
        String responseBody = readResponseBody(connection);
        Task retrievedTask = gson.fromJson(responseBody, Task.class);
        assertEquals(task.getName(), retrievedTask.getName());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
    }

    @Test
    void testGetTaskWithInvalidId() throws IOException {
        URL url = new URL("http://localhost:8080/tasks/122"); // Некорректный ID
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        assertEquals(404, responseCode); // Ожидаем код 400 (ошибка клиента)
    }

    @Test
    void testGetSubTasks() throws IOException {
        URL url = new URL("http://localhost:8080/subtasks");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
        String responseBody = readResponseBody(connection);
        assertEquals("[]", responseBody); // Ожидаем пустой список
    }

    @Test
    void testPostSubtask() throws IOException {
        URL url = new URL("http://localhost:8080/subtasks");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        URL url1 = new URL("http://localhost:8080/epics");
        HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
        connection1.setRequestMethod("POST");
        connection1.setDoOutput(true);
        connection1.setRequestProperty("Content-Type", "application/json");
        Epic epic = new Epic("Epic task", "Description");
        String jsonEpic = gson.toJson(epic);
        System.out.println(jsonEpic);
        connection1.getOutputStream().write(jsonEpic.getBytes());
        //Создаём задачу и отправляем её в запросе
        SubTask subtask = new SubTask("Test Task", "Description", null, StatusCodes.NEW,
                1, Duration.ofHours(4),
                LocalDateTime.of(2024, 12, 20, 12, 0, 0));
        String jsonTask = gson.toJson(subtask);
        connection.getOutputStream().write(jsonTask.getBytes());
        int responseCode1 = connection1.getResponseCode();
        assertEquals(201, responseCode1);
        int responseCode = connection.getResponseCode();
        assertEquals(201, responseCode);
    }

    @Test
    void testPostSubTaskWithConflictingStartTime() throws IOException {
        URL url1 = new URL("http://localhost:8080/epics");
        HttpURLConnection connection11 = (HttpURLConnection) url1.openConnection();
        connection11.setRequestMethod("POST");
        connection11.setDoOutput(true);
        connection11.setRequestProperty("Content-Type", "application/json");
        Epic epic = new Epic("Epic task", "Description");
        String jsonEpic = gson.toJson(epic);
        System.out.println(jsonEpic);
        connection11.getOutputStream().write(jsonEpic.getBytes());
        int responseCode11 = connection11.getResponseCode();
        assertEquals(201, responseCode11, "Failed to post the first task");
        URL url = new URL("http://localhost:8080/subtasks");
        HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();
        connection1.setRequestMethod("POST");
        connection1.setDoOutput(true);
        connection1.setRequestProperty("Content-Type", "application/json");
        SubTask subtask = new SubTask("Test Task", "Description", null, StatusCodes.NEW,
                1, Duration.ofHours(4),
                LocalDateTime.of(2024, 12, 20, 12, 0, 0));
        String jsonTask1 = gson.toJson(subtask);
        connection1.getOutputStream().write(jsonTask1.getBytes());
        int responseCode1 = connection1.getResponseCode();
        assertEquals(201, responseCode1, "Failed to post the first task");
        String responseBody1 = readResponseBody(connection1);
        assertTrue(responseBody1.contains("\"id\":"), "First task ID not returned");
        // Постим вторую задачу с таким же startTime
        HttpURLConnection connection2 = (HttpURLConnection) url.openConnection();
        connection2.setRequestMethod("POST");
        connection2.setDoOutput(true);
        connection2.setRequestProperty("Content-Type", "application/json");
        SubTask subtask1 = new SubTask("Test Task11", "Description", null, StatusCodes.NEW,
                1, Duration.ofHours(4),
                LocalDateTime.of(2024, 12, 20, 13, 0, 0));
        String jsonTask2 = gson.toJson(subtask1);
        System.out.println(jsonTask2);
        connection2.getOutputStream().write(jsonTask2.getBytes());
        int responseCode2 = connection2.getResponseCode();
        assertEquals(406, responseCode2, "Expected 406 for conflicting task start time");
    }

    @Test
    void testDeleteSubTask() throws IOException {
        // Сначала добавляем задачу
        URL url = new URL("http://localhost:8080/subtasks");
        HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();
        connection1.setRequestMethod("POST");
        connection1.setDoOutput(true);
        connection1.setRequestProperty("Content-Type", "application/json");
        SubTask subtask = new SubTask("Test Task", "Description", null, StatusCodes.NEW,
                1, Duration.ofHours(4),
                LocalDateTime.of(2024, 12, 20, 12, 0, 0));
        String jsonTask1 = gson.toJson(subtask);
        connection1.getOutputStream().write(jsonTask1.getBytes());
        int responseCode1 = connection1.getResponseCode();
        assertEquals(201, responseCode1, "Failed to post the first task");
        // Удаляем задачу
        URL url1 = new URL("http://localhost:8080/tasks/" + "1");
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
        connection.setRequestMethod("DELETE");
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
        String responseBody = readResponseBody(connection);
        assertEquals("{\"status\": \"Task deleted\"}", responseBody);
    }

    @Test
    void testGetSubTaskWithInvalidId() throws IOException {
        URL url = new URL("http://localhost:8080/subtasks/122"); // Некорректный ID
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        assertEquals(404, responseCode); // Ожидаем код 400 (ошибка клиента)
    }

    @Test
    void testCreateEpic() throws IOException {
        // Создаем новый эпик
        Epic epic = new Epic("Test Epic", "This is a test epic");
        String jsonEpic = gson.toJson(epic);
        // Создаем соединение для POST-запроса
        URL url = new URL("http://localhost:8080/epics");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        // Отправляем данные эпика в запросе
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonEpic.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        // Получаем код ответа
        int responseCode = connection.getResponseCode();
        assertEquals(201, responseCode, "Failed to create Epic");
        // Проверяем, что ID эпика возвращен в ответе
        String responseBody = readResponseBody(connection);
        assertTrue(responseBody.contains("\"id\":"), "ID not returned in the response");
    }

    @Test
    void testGetAllEpics() throws IOException {
        // Создаем запрос для получения всех эпиков
        URL url = new URL("http://localhost:8080/epics");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // Получаем код ответа
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode, "Failed to fetch epics");
        // Проверяем, что в ответе есть список эпиков
        String responseBody = readResponseBody(connection);
        assertTrue(responseBody.contains("["), "No epics returned");
        assertTrue(responseBody.contains("]"), "No epics returned");
    }

    @Test
    void testGetEpicById() throws IOException {
        // Создаем новый эпик для тестирования
        Epic epic = new Epic("Test Epic", "This is a test epic");
        String jsonEpic = gson.toJson(epic);
        URL url1 = new URL("http://localhost:8080/epics");
        HttpURLConnection connection11 = (HttpURLConnection) url1.openConnection();
        connection11.setRequestMethod("POST");
        connection11.setDoOutput(true);
        connection11.setRequestProperty("Content-Type", "application/json");
        connection11.getOutputStream().write(jsonEpic.getBytes());
        int responseCode = connection11.getResponseCode();
        assertEquals(201, responseCode, "Failed to create Epic");
        String epicId = "1";
        // Создаем GET-запрос для получения эпика по ID
        URL getUrl = new URL("http://localhost:8080/epics/" + epicId);
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        int getResponseCode = getConnection.getResponseCode();
        assertEquals(200, getResponseCode, "Failed to fetch epic by ID");
    }

    @Test
    void testDeleteEpic() throws IOException {
        Epic epic = new Epic("Test Epic", "This is a test epic");
        String jsonEpic = gson.toJson(epic);
        URL url1 = new URL("http://localhost:8080/epics");
        HttpURLConnection connection11 = (HttpURLConnection) url1.openConnection();
        connection11.setRequestMethod("POST");
        connection11.setDoOutput(true);
        connection11.setRequestProperty("Content-Type", "application/json");
        connection11.getOutputStream().write(jsonEpic.getBytes());
        int responseCode = connection11.getResponseCode();
        assertEquals(201, responseCode, "Failed to create Epic");
        String epicId = "1";
        // Создаем DELETE-запрос для удаления эпика по ID
        URL deleteUrl = new URL("http://localhost:8080/epics/" + epicId);
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        // Получаем код ответа
        int deleteResponseCode = deleteConnection.getResponseCode();
        assertEquals(200, deleteResponseCode, "Failed to delete Epic");
        // Попытка получить удаленный эпик по ID (должен вернуть 404)
        URL getDeletedUrl = new URL("http://localhost:8080/epics/" + epicId);
        HttpURLConnection getDeletedConnection = (HttpURLConnection) getDeletedUrl.openConnection();
        getDeletedConnection.setRequestMethod("GET");
        int getDeletedResponseCode = getDeletedConnection.getResponseCode();
        assertEquals(404, getDeletedResponseCode, "Deleted epic should not be found");
    }

    @Test
    void testGetHistory() throws IOException {
        // Создаем новый эпик для тестирования
        Epic epic = new Epic("Test Epic", "This is a test epic");
        String jsonEpic = gson.toJson(epic);
        URL url1 = new URL("http://localhost:8080/epics");
        HttpURLConnection connection11 = (HttpURLConnection) url1.openConnection();
        connection11.setRequestMethod("POST");
        connection11.setDoOutput(true);
        connection11.setRequestProperty("Content-Type", "application/json");
        connection11.getOutputStream().write(jsonEpic.getBytes());
        int responseCode = connection11.getResponseCode();
        assertEquals(201, responseCode, "Failed to create Epic");
        String epicId = "1";
        URL getUrl = new URL("http://localhost:8080/epics/" + epicId);
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        int getResponseCode = getConnection.getResponseCode();
        assertEquals(200, getResponseCode, "Failed to fetch epic by ID");
        URL historyUrl = new URL("http://localhost:8080/history");
        HttpURLConnection historyConnection = (HttpURLConnection) historyUrl.openConnection();
        historyConnection.setRequestMethod("GET");
        int historyResponseCode = historyConnection.getResponseCode();
        assertEquals(200, historyResponseCode, "Failed to fetch history");
    }

    @Test
    void testGetPrioritized() throws IOException {
        Epic epic = new Epic("Epic Task", "Description of Epic");
        String jsonEpic = gson.toJson(epic);
        URL epicUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection epicConnection = (HttpURLConnection) epicUrl.openConnection();
        epicConnection.setRequestMethod("POST");
        epicConnection.setDoOutput(true);
        epicConnection.setRequestProperty("Content-Type", "application/json");
        epicConnection.getOutputStream().write(jsonEpic.getBytes());
        int epicResponseCode = epicConnection.getResponseCode();
        assertEquals(201, epicResponseCode, "Failed to create Epic");
        SubTask subtask1 = new SubTask("SubTask 1", "Description of SubTask 1",
                null, StatusCodes.NEW,
                1, Duration.ofHours(4), LocalDateTime.of(2024, 12, 20, 12,
                0, 0));
        SubTask subtask2 = new SubTask("SubTask 2", "Description of SubTask 2", null,
                StatusCodes.NEW,
                1, Duration.ofHours(5),
                LocalDateTime.of(2024, 12, 21, 10, 0, 0));
        String jsonSubTask1 = gson.toJson(subtask1);
        String jsonSubTask2 = gson.toJson(subtask2);
        URL subtaskUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection subtaskConnection1 = (HttpURLConnection) subtaskUrl.openConnection();
        subtaskConnection1.setRequestMethod("POST");
        subtaskConnection1.setDoOutput(true);
        subtaskConnection1.setRequestProperty("Content-Type", "application/json");
        subtaskConnection1.getOutputStream().write(jsonSubTask1.getBytes());
        int subtaskResponseCode1 = subtaskConnection1.getResponseCode();
        assertEquals(201, subtaskResponseCode1, "Failed to create SubTask 1");
        HttpURLConnection subtaskConnection2 = (HttpURLConnection) subtaskUrl.openConnection();
        subtaskConnection2.setRequestMethod("POST");
        subtaskConnection2.setDoOutput(true);
        subtaskConnection2.setRequestProperty("Content-Type", "application/json");
        subtaskConnection2.getOutputStream().write(jsonSubTask2.getBytes());
        int subtaskResponseCode2 = subtaskConnection2.getResponseCode();
        assertEquals(201, subtaskResponseCode2, "Failed to create SubTask 2");
        URL prioritizedUrl = new URL("http://localhost:8080/prioritized");
        HttpURLConnection prioritizedConnection = (HttpURLConnection) prioritizedUrl.openConnection();
        prioritizedConnection.setRequestMethod("GET");
        int prioritizedResponseCode = prioritizedConnection.getResponseCode();
        assertEquals(200, prioritizedResponseCode, "Failed to fetch prioritized tasks");
        String prioritizedResponseBody = readResponseBody(prioritizedConnection);
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> prioritizedTasks = gson.fromJson(prioritizedResponseBody, listType);
        assertNotNull(prioritizedTasks, "No prioritized tasks returned");
        assertEquals(2, prioritizedTasks.size(), "Expected 2 tasks in prioritized list");
        assertTrue(prioritizedTasks.get(0).getStartTime().isBefore(prioritizedTasks.get(1).getStartTime()),
                "Tasks are not sorted by start time");
        prioritizedTasks.forEach(task -> System.out.println(task.getName() + " - " + task.getStartTime()));
    }

    // Вспомогательный метод для добавления задачи и получения её ID
    private String addTaskAndGetId(Task task) throws IOException {
        URL url = new URL("http://localhost:8080/tasks");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        String jsonTask = gson.toJson(task);
        connection.getOutputStream().write(jsonTask.getBytes());
        int responseCode = connection.getResponseCode();
        assertEquals(201, responseCode);
        String responseBody = readResponseBody(connection);
        return gson.fromJson(responseBody, TaskIdResponse.class).getId();
    }

    // Вспомогательный метод для чтения тела ответа
    private String readResponseBody(HttpURLConnection connection) throws IOException {
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    // Класс для десериализации ответа с ID задачи
    private static class TaskIdResponse {
        private String id;

        public String getId() {
            return id;
        }
    }
}
