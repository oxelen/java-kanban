package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import managers.TaskManager;
import task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends StandardOperationsTaskHandler implements HttpHandler {
    public EpicHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        super.h = h;
        String[] path = h.getRequestURI().getPath().split("/");
        if (h.getRequestMethod().equals("GET")
                && path.length == 4
                && path[3].equals("subtasks")) {
            handleGetEpicSubTasks();
        } else super.handle(h);
    }

    @Override
    void handleGetAllTasks() throws IOException {
        sendText(gson.toJson(manager.getAllEpic()), 200);
    }

    @Override
    void handleGetTaskById() throws IOException, NotFoundException {
        int id = getId();
        sendText(gson.toJson(manager.getEpicById(id)), 200);
    }

    @Override
    void handleCreateTask() throws IOException, IntersectionException {
        Epic epic = getEpic();
        manager.addEpic(epic);
        sendText("Epic added. id=" + epic.getId(), 201);
    }

    @Override
    void handleUpdateTask() throws NotFoundException {
        throw new NotFoundException();
    }

    @Override
    void handleDeleteTask() throws IOException, NotFoundException {
        int id = getId();
        manager.deleteEpicById(id);

        sendText("Epic deleted. id=" + id, 200);
    }

    void handleGetEpicSubTasks() throws IOException {
        try {
            int id = getId();
            sendText(gson.toJson(manager.getSubtasksByEpicId(id)), 200);
        } catch (NumberFormatException e) {
            sendText("parameter id is not a number", 406);
        } catch (NotFoundException e) {
            sendNotFound();
        }
    }

    private Epic getEpic() throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, Epic.class);
    }
}
