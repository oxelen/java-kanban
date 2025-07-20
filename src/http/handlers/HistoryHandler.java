package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import managers.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        super.h = h;

        if (h.getRequestMethod().equals("GET") && h.getRequestURI().getPath().equals("/history")) {
            try {
                sendText(gson.toJson(manager.getHistory()), 200);
            } catch (NotFoundException e) {
                sendNotFound();
            }
        } else sendNotFound();
    }
}
