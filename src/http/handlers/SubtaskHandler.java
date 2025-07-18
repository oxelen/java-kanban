package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import managers.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends StandardOperationsTaskHandler implements HttpHandler {
    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        super.h = h;
        super.handle(h);
    }

    @Override
    void handleGetAllTasks() throws IOException {
        sendText(gson.toJson(manager.getAllSubtask()), 200);
    }

    @Override
    void handleGetTaskById() throws IOException, NotFoundException {
            int id = getId();
            sendText(gson.toJson(manager.getSubtaskById(id)), 200);
    }

    @Override
    void handleCreateTask() throws IOException, IntersectionException, NotFoundException {
            Subtask subtask = getSubtask();
            manager.addSubtask(subtask);
            sendText("Subtask added. id=" + subtask.getId(), 201);
    }

    @Override
    void handleUpdateTask() throws IOException, IntersectionException, NotFoundException {
            int id = getId();
            Subtask subtask = getSubtask();
            if (id != subtask.getId()) {
                sendText("id from URI is not equals id from body", 406);
                return;
            }
            manager.updateSubtask(subtask);

            sendText("Subtask updated. id=" + subtask.getId(), 201);
    }

    @Override
    void handleDeleteTask() throws IOException, NotFoundException {
            int id = getId();
            manager.deleteSubtaskById(id);

            sendText("Subtask deleted. id=" + id, 200);
    }

    private Subtask getSubtask() throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, Subtask.class);
    }
}
