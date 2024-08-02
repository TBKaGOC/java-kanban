package main.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.exception.IntersectionOfTasksException;
import main.exception.NotFoundException;
import main.manager.InMemoryTaskManager;
import main.manager.TaskManager;
import main.model.Task;

import java.io.*;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    public TasksHandler(TaskManager manager) {
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
                        Task task = manager.getTask(taskId).orElseThrow(NotFoundException::new);
                        String gsonTask = gson.toJson(task);

                        sendText(gsonTask, exchange);
                    } else {
                        String gsonTaskList = gson.toJson(manager.getTasks());
                        sendText(gsonTaskList, exchange);
                    }
                    break;
                case "POST":
                    try {
                        String jsonElement = new String(exchange.getRequestBody().readAllBytes(), charset);

                        Task task = gson.fromJson(jsonElement, Task.class);
                        if (task.getId() == 0) {
                            task.setId(InMemoryTaskManager.getNewId());
                            manager.addTask(task);
                        } else {
                            manager.updatingTask(task);
                        }

                        exchange.sendResponseHeaders(201, 0);
                    } catch (IntersectionOfTasksException e) {
                        sendHasInteractions(exchange);
                    }
                    break;
                case "DELETE":
                    String[] elementsOfDeletePath = exchange.getRequestURI().getPath().split("/");
                    int deleteTaskId = Integer.parseInt(elementsOfDeletePath[2]);

                    manager.deleteTask(deleteTaskId);

                    exchange.sendResponseHeaders(200, 0);
                    break;
                default:
                    sendUnprocessedCommand(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            exchange.close();
        }
    }
}
