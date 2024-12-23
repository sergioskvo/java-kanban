package controllers;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static HttpServer server;
    protected static TaskManager taskManager = Managers.getDefault();
    protected static HistoryManager historyManager = Managers.getDefaultHistory();

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        // Привязка обработчиков
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    //    public static void main(String[] args) throws IOException {
//        HttpTaskServer httpTaskServer = new HttpTaskServer();
//        httpTaskServer.start();
//    }
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
}
