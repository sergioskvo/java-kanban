package controllers;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler implements HttpHandler {
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

    protected void sendMethodNotAllowed(HttpExchange exchange, String message) throws IOException {
        String response = "{\"error\": \"" + message + "\"}";
        sendResponse(exchange, 405, response);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                processGet(exchange);
                break;
            case "POST":
                processPost(exchange);
                break;
            case "DELETE":
                processDelete(exchange);
                break;
            default:
                sendNotFound(exchange, "Resource not found");
        }

    }

    protected void processGet(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange, "Method Not Allowed");
    }

    protected void processPost(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange, "Method Not Allowed");
    }

    protected void processDelete(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange, "Method Not Allowed");
    }
}
