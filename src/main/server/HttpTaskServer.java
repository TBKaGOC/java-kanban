package main.server;

import com.sun.net.httpserver.HttpServer;
import main.manager.Managers;
import main.manager.TaskManager;
import main.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static HttpServer server;

    public static final int PORT = 8080;

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost",PORT), 0);

        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/subtasks", new SubtasksHandler(manager));
        server.createContext("/epics", new EpicsHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());

        httpTaskServer.start();
        Thread.sleep(60000);
        httpTaskServer.stop();
    }

    public void start() {
        System.out.println("Started server " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Stopped server " + PORT);
    }
}
