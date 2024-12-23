package controllers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskOverlapException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class EpicHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestURI().getPath()
                    .matches("/epics/\\d+/subtasks")) {
                handleGetSubtasksFromEpic(exchange);
            } else if (exchange.getRequestURI().getPath()
                    .matches("/epics/\\d+")) {
                handleGetEpicById(exchange);
            } else if (exchange.getRequestURI().getPath().equals("/epics")) {
                handleGetEpicsList(exchange);
            } else {
                sendNotFound(exchange, "Resource not found");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Internal Server Error");
        }

    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestURI().getPath().equals("/epics")) {
                handlePostRequest(exchange);
            } else {
                sendNotFound(exchange, "Resource not found");
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Internal Server Error");
        }
    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestURI().getPath()
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
