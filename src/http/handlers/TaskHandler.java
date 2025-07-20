package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import managers.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends StandardOperationsTaskHandler implements HttpHandler {
    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        super.h = h;
        super.handle(h);
    }

    @Override
    protected void handleGetAllTasks() throws IOException {
        sendText(gson.toJson(manager.getAllTask()), 200);
    }

    @Override
    protected void handleGetTaskById() throws IOException, NotFoundException {
            int id = getId();
            sendText(gson.toJson(manager.getTaskById(id)), 200);
    }

    @Override
    protected void handleCreateTask() throws IOException, IntersectionException {
            Task task = getTask();
            manager.addTask(task);
            sendText("Task added. id=" + task.getId(), 201);
    }

    @Override
    protected void handleUpdateTask() throws IOException, IntersectionException, NotFoundException {
            int id = getId();
            Task task = getTask();
            if (id != task.getId()) sendText("id from URI is not equals id from body", 406);
            manager.updateTask(task);

            sendText("Task updated. id=" + id, 201);
    }

    @Override
    protected void handleDeleteTask() throws IOException, NotFoundException {
            int id = getId();
            manager.deleteTaskById(id);

            sendText("task deleted. id=" + id, 200);
    }

    private Task getTask() throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, Task.class);
    }

}
