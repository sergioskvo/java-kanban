package controllers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskOverlapException;
import model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestURI().getPath()
                    .matches("/tasks/\\d+")) {
                handleGetTaskById(exchange);
            } else {
                handleGetRequest(exchange);
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Internal Server Error");
        }
    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        try {
                handlePostRequest(exchange);
        } catch (TaskOverlapException e) {
            sendConflict(exchange, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Internal Server Error");
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestURI().getPath()
                    .matches("/tasks/\\d+")) {
                handleDeleteRequest(exchange);
            } else {
                sendNotFound(exchange, "Resource not found");
            }
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
