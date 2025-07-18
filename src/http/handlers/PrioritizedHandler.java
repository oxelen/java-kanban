package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager;
    Gson gson;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        super.h = h;

        if (h.getRequestMethod().equals("GET")
                && h.getRequestURI().getPath().equals("/prioritized")) {
            sendText(gson.toJson(manager.getPrioritizedTasks()), 200);
        } else sendNotFound();
    }
}
