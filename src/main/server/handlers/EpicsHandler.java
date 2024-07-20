package main.server.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.exception.NotFoundException;
import main.manager.InMemoryTaskManager;
import main.manager.TaskManager;
import main.model.EpicTask;
import main.model.Subtask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    String[] elementsOfPath = exchange.getRequestURI().getPath().split("/");
                    if (elementsOfPath.length == 3) {
                        int taskId = Integer.parseInt(elementsOfPath[2]);
                        EpicTask task = manager.getEpicTask(taskId).orElseThrow(NotFoundException::new);
                        String gsonTask = gson.toJson(task);

                        sendText(gsonTask, exchange);
                    } else if (elementsOfPath.length == 4 && elementsOfPath[3].equals("subtasks")) {
                        int taskId = Integer.parseInt(elementsOfPath[2]);
                        Map<Integer, Subtask> subtasks = manager.getSubtasksOfEpic(taskId);

                        String gsonSubtasks = gson.toJson(subtasks);
                        sendText(gsonSubtasks, exchange);
                    } else {
                        String gsonTaskList = gson.toJson(manager.getEpicTasks());
                        sendText(gsonTaskList, exchange);
                    }
                    break;
                case "POST":
                    try (BufferedReader stream = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                        JsonElement jsonElement = JsonParser.parseReader(stream);

                        if (jsonElement.isJsonObject()) {
                            EpicTask task = gson.fromJson(jsonElement, EpicTask.class);
                                task.setId(InMemoryTaskManager.getNewId());
                                manager.addEpicTask(task);

                            exchange.sendResponseHeaders(201, 0);
                        }
                    }
                    break;
                case "DELETE":
                    String[] elementsOfDeletePath = exchange.getRequestURI().getPath().split("/");

                    manager.deleteEpicTask(Integer.parseInt(elementsOfDeletePath[2]));

                    exchange.sendResponseHeaders(200, 0);
                    break;
                default:
                    sendUnprocessedCommand(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}
