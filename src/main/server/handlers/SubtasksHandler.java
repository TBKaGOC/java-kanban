package main.server.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.exception.IntersectionOfTasksException;
import main.exception.NotFoundException;
import main.manager.InMemoryTaskManager;
import main.manager.TaskManager;
import main.model.Subtask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHandler(TaskManager manager) {
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
                        Subtask task = manager.getSubtask(taskId).orElseThrow(NotFoundException::new);
                        String gsonTask = gson.toJson(task);

                        sendText(gsonTask, exchange);
                    } else {
                        String gsonTaskList = gson.toJson(manager.getSubtasks());
                        sendText(gsonTaskList, exchange);
                    }
                    break;
                case "POST":
                    try (BufferedReader stream = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                        JsonElement jsonElement = JsonParser.parseReader(stream);

                        if (jsonElement.isJsonObject()) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            if (jsonObject.has("epicTaskId")) {
                                int epicId = jsonObject.get("epicTaskId").getAsInt();
                                Subtask subtask = gson.fromJson(jsonObject.get("subtask"), Subtask.class);
                                subtask.setId(InMemoryTaskManager.getNewId());

                                manager.addSubtask(subtask, epicId);
                            } else {
                                Subtask subtask = gson.fromJson(jsonElement, Subtask.class);

                                manager.updatingSubtask(subtask);
                            }

                            exchange.sendResponseHeaders(201, 0);
                        }
                    } catch (IntersectionOfTasksException e) {
                        sendHasInteractions(exchange);
                    }
                    break;
                case "DELETE":
                    String[] elementsOfDeletePath = exchange.getRequestURI().getPath().split("/");
                    int deleteTaskId = Integer.parseInt(elementsOfDeletePath[2]);

                    manager.deleteSubtask(deleteTaskId);

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
