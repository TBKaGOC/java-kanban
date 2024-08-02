package main.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            String gsonSortedTask = gson.toJson(manager.getSortedTask());
            sendText(gsonSortedTask, exchange);
        } else {
            sendUnprocessedCommand(exchange);
        }

        exchange.close();
    }
}
