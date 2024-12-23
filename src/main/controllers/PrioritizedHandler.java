package controllers;

import com.sun.net.httpserver.HttpExchange;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGet(HttpExchange exchange) throws IOException {
        try {
            handleGetRequest(exchange);
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
