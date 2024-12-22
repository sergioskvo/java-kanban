package controllers;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;
import exceptions.TaskOverlapException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static HttpServer server;
    protected static TaskManager taskManager = Managers.getDefault();
    protected static HistoryManager historyManager = Managers.getDefaultHistory();

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Привязка обработчиков
        server.createContext("/tasks", new TaskHandler());
        server.createContext("/subtasks", new SubtaskHandler());
        server.createContext("/epics", new EpicHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
    }

//    public static void main(String[] args) throws IOException {
//        HttpTaskServer httpTaskServer = new HttpTaskServer();
//        httpTaskServer.start();
//    }

    // Метод для запуска сервера
    public void start() {
        if (server != null) {
            server.start();
            System.out.println("Server started on port " + PORT);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped");
        }
    }

    // Обработчик для /tasks
    static class TaskHandler extends BaseHttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String requestMethod = exchange.getRequestMethod();
                System.out.println(requestMethod);
                if ("GET".equals(requestMethod) && exchange.getRequestURI().getPath()
                        .matches("/tasks/\\d+")) {
                    handleGetTaskById(exchange);
                } else if ("GET".equals(requestMethod)) {
                    System.out.println(("GET".equals(requestMethod)));
                    handleGetRequest(exchange);
                } else if ("POST".equals(requestMethod)) {
                    handlePostRequest(exchange);
                } else if ("DELETE".equals(requestMethod) && exchange.getRequestURI().getPath()
                        .matches("/tasks/\\d+")) {
                    handleDeleteRequest(exchange);
                } else {
                    sendNotFound(exchange, "Resource not found");
                }
            } catch (TaskOverlapException e) {
                sendConflict(exchange, e.getMessage());
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            List<Task> tasks = taskManager.getTasksList();
            String jsonResponse = gson.toJson(tasks);
            sendResponse(exchange, 200, jsonResponse);
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int taskId = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTaskViaId(taskId);
            if (task != null) {
                String jsonResponse = gson.toJson(task);
                sendResponse(exchange, 200, jsonResponse);
            } else {
                sendNotFound(exchange, "Task not found");
            }
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            Task taskFromRequest = gson.fromJson(reader, Task.class);
            if (taskFromRequest.getIdNumber() != null) {
                try {
                    taskManager.refreshTask(taskFromRequest);
                    String jsonResponse = "{\"status\": \"Task updated\", \"id\": " +
                            taskFromRequest.getIdNumber() + "}";
                    sendResponse(exchange, 200, jsonResponse);
                } catch (Exception e) {
                    sendErrorResponse(exchange, 400, "Failed to update task");
                }
            } else {
                try {
                    int taskId = taskManager.saveTask(taskFromRequest);
                    String jsonResponse = "{\"id\": " + taskId + "}";
                    sendResponse(exchange, 201, jsonResponse);
                } catch (TaskOverlapException e) {
                    sendConflict(exchange, e.getMessage());
                }
            }
        }

        private void handleDeleteRequest(HttpExchange exchange) throws IOException {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int taskId = Integer.parseInt(pathParts[2]);
            taskManager.deleteViaId(taskId);
            String response = "{\"status\": \"Task deleted\"}";
            sendResponse(exchange, 200, response);
        }
    }

    // Обработчик для /subtasks
    static class SubtaskHandler extends BaseHttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String requestMethod = exchange.getRequestMethod();
                if ("GET".equals(requestMethod) && exchange.getRequestURI().getPath()
                        .matches("/subtasks/\\d+")) {
                    handleGetSubtaskById(exchange);
                } else if ("GET".equals(requestMethod)) {
                    handleGetRequest(exchange);
                } else if ("POST".equals(requestMethod)) {
                    handlePostRequest(exchange);
                } else if ("DELETE".equals(requestMethod) && exchange.getRequestURI().getPath()
                        .matches("/subtasks/\\d+")) {
                    handleDeleteRequest(exchange);
                } else {
                    sendNotFound(exchange, "Resource not found");
                }
            } catch (TaskOverlapException e) {
                sendConflict(exchange, e.getMessage());
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            List<SubTask> subtasks = taskManager.getSubTasksList();
            String jsonResponse = gson.toJson(subtasks);
            sendResponse(exchange, 200, jsonResponse);
        }

        private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int subtaskId = Integer.parseInt(pathParts[2]);
            Task subtask = taskManager.getTaskViaId(subtaskId);
            if (subtask != null) {
                String jsonResponse = gson.toJson((SubTask) subtask);
                sendResponse(exchange, 200, jsonResponse);
            } else {
                sendNotFound(exchange, "Subtask not found");
            }
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            SubTask subtaskFromRequest = gson.fromJson(reader, SubTask.class);
            System.out.println(subtaskFromRequest);
            if (subtaskFromRequest.getIdNumber() != null) {
                try {
                    taskManager.refreshTask(subtaskFromRequest);
                    String jsonResponse = "{\"status\": \"Subtask updated\", \"id\": " +
                            subtaskFromRequest.getIdNumber() + "}";
                    sendResponse(exchange, 200, jsonResponse);
                } catch (Exception e) {
                    sendErrorResponse(exchange, 400, "Failed to update subtask");
                }
            } else {
                try {
                    Integer subtaskId = taskManager.saveTask(subtaskFromRequest);
                    String jsonResponse = "{\"id\": " + subtaskId + "}";
                    sendResponse(exchange, 201, jsonResponse);
                } catch (TaskOverlapException e) {
                    sendConflict(exchange, e.getMessage());
                }
            }
        }

        private void handleDeleteRequest(HttpExchange exchange) throws IOException {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int subtaskId = Integer.parseInt(pathParts[2]);
            taskManager.deleteViaId(subtaskId);
            String response = "{\"status\": \"Subtask deleted\"}";
            sendResponse(exchange, 200, response);
        }
    }

    // Обработчик для /epics
    static class EpicHandler extends BaseHttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String requestMethod = exchange.getRequestMethod();
                if ("GET".equals(requestMethod) && exchange.getRequestURI().getPath()
                        .matches("/epics/\\d+/subtasks")) {
                    handleGetSubtasksFromEpic(exchange);
                } else if ("GET".equals(requestMethod) && exchange.getRequestURI().getPath()
                        .matches("/epics/\\d+")) {
                    handleGetEpicById(exchange);
                } else if ("GET".equals(requestMethod) && exchange.getRequestURI().getPath().equals("/epics")) {
                    handleGetEpicsList(exchange);
                } else if ("POST".equals(requestMethod) && exchange.getRequestURI().getPath().equals("/epics")) {
                    handlePostRequest(exchange);
                } else if ("DELETE".equals(requestMethod) && exchange.getRequestURI().getPath()
                        .matches("/epics/\\d+")) {
                    handleDeleteRequest(exchange);
                } else {
                    sendNotFound(exchange, "Resource not found");
                }
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleGetEpicsList(HttpExchange exchange) throws IOException {
            List<Epic> epics = taskManager.getEpicsList();
            String jsonResponse = gson.toJson(epics);
            sendResponse(exchange, 200, jsonResponse);
        }

        private void handleGetEpicById(HttpExchange exchange) throws IOException {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int epicId = Integer.parseInt(pathParts[2]);
            Task epic = taskManager.getTaskViaId(epicId);
            if (epic != null && epic instanceof Epic) {
                String jsonResponse = gson.toJson((Epic) epic);
                sendResponse(exchange, 200, jsonResponse);
            } else {
                sendNotFound(exchange, "Epic not found");
            }
        }

        private void handleGetSubtasksFromEpic(HttpExchange exchange) throws IOException {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int epicId = Integer.parseInt(pathParts[2]);
            Task epic = taskManager.getTaskViaId(epicId);
            if (epic != null && epic instanceof Epic) {
                Map<Integer, SubTask> subtasks = taskManager.getSubTasksFromEpic(epicId);
                String jsonResponse = gson.toJson(subtasks);
                sendResponse(exchange, 200, jsonResponse);
            } else {
                sendNotFound(exchange, "Epic not found");
            }
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            Epic epicFromRequest = gson.fromJson(reader, Epic.class);
            try {
                int epicId = taskManager.saveTask(epicFromRequest);
                String jsonResponse = "{\"id\": " + epicId + "}";
                sendResponse(exchange, 201, jsonResponse);
            } catch (TaskOverlapException e) {
                sendConflict(exchange, e.getMessage());
            }
        }

        private void handleDeleteRequest(HttpExchange exchange) throws IOException {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            int epicId = Integer.parseInt(pathParts[2]);
            Task epic = taskManager.getTaskViaId(epicId);
            if (epic != null && epic instanceof Epic) {
                taskManager.deleteViaId(epicId);
                String response = "{\"status\": \"Epic deleted\"}";
                sendResponse(exchange, 200, response);
            } else {
                sendNotFound(exchange, "Epic not found");
            }
        }
    }

    // Обработчик для /history
    static class HistoryHandler extends BaseHttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String requestMethod = exchange.getRequestMethod();
                if ("GET".equals(requestMethod)) {
                    handleGetRequest(exchange);
                } else {
                    sendNotFound(exchange, "Resource not found");
                }
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            List<Task> history = taskManager.getTasks();
            String jsonResponse = gson.toJson(history);
            sendResponse(exchange, 200, jsonResponse);
        }
    }

    // Обработчик для /prioritized
    static class PrioritizedHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String requestMethod = exchange.getRequestMethod();
                if ("GET".equals(requestMethod)) {
                    handleGetRequest(exchange);
                } else {
                    sendNotFound(exchange, "Resource not found");
                }
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, "Internal Server Error");
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            String jsonResponse = gson.toJson(prioritizedTasks);
            sendResponse(exchange, 200, jsonResponse);
        }
    }

    // Базовый обработчик для отправки стандартных ответов
    public static abstract class BaseHttpHandler implements HttpHandler {

        //        protected final TaskManager taskManager = Managers.getDefault();
//        protected final HistoryManager historyManager = Managers.getDefaultHistory();
        protected final Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            byte[] responseBytes = response.getBytes();
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
            exchange.close();
        }

        protected void sendNotFound(HttpExchange exchange, String errorMessage) throws IOException {
            String response = "{\"error\": \"" + errorMessage + "\"}";
            sendResponse(exchange, 404, response);
        }

        protected void sendConflict(HttpExchange exchange, String errorMessage) throws IOException {
            String response = "{\"error\": \"" + errorMessage + "\"}";
            sendResponse(exchange, 406, response);
        }

        protected void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            String response = "{\"error\": \"" + message + "\"}";
            sendResponse(exchange, statusCode, response);
        }
    }
}
