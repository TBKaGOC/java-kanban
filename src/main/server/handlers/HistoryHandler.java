package main.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            String gsonSortedTask = gson.toJson(manager.getHistory().getHistory());
            sendText(gsonSortedTask, exchange);
        } else {
            sendUnprocessedCommand(exchange);
        }

        exchange.close();
    }
}
