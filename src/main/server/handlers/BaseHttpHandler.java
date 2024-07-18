package main.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import main.manager.TaskManager;
import main.server.adapters.DurationAdapter;
import main.server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    public static final Charset charset = StandardCharsets.UTF_8;
    public static final Gson gson = new GsonBuilder().registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    protected final TaskManager manager;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    public void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);

        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write("The tasks should not intersections.".getBytes(charset));
        }
    }

    public void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);

        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write("The task not founded.".getBytes(charset));
        }
    }

    public void sendUnprocessedCommand(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, 0);

        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write("An unprocessed command".getBytes(charset));
        }
    }

    public void sendText(String text, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);

        try (OutputStream stream = exchange.getResponseBody()) {
            stream.write(text.getBytes(charset));
        }
    }
}
