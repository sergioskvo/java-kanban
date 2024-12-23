package controllers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskOverlapException;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestURI().getPath()
                    .matches("/subtasks/\\d+")) {
                handleGetSubtaskById(exchange);
            } else {
                handleGetRequest(exchange);
            }
        } catch (TaskOverlapException e) {
            sendConflict(exchange, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Internal Server Error");
        }

    }

    @Override
    protected void processPost(HttpExchange exchange) throws IOException {
        try {
            handlePostRequest(exchange);
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Internal Server Error");
        }

    }

    @Override
    protected void processDelete(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestURI().getPath()
                    .matches("/subtasks/\\d+")) {
                handleDeleteRequest(exchange);
            } else {
                sendNotFound(exchange, "Resource not found");
            }
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
