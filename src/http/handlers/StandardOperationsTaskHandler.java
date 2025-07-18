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

public abstract class StandardOperationsTaskHandler extends BaseHttpHandler implements HttpHandler {
    protected TaskManager manager;
    protected final Gson gson;

    protected StandardOperationsTaskHandler(TaskManager manager, Gson gson) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        Endpoint endpoint = getEndpoint(h.getRequestMethod(), h.getRequestURI().getPath());
        try {
            switch (endpoint) {
                case GET_ALL_TASKS:
                    handleGetAllTasks();
                    break;
                case GET_TASK_BY_ID:
                    handleGetTaskById();
                    break;
                case CREATE_TASK:
                    handleCreateTask();
                    break;
                case UPDATE_TASK:
                    handleUpdateTask();
                    break;
                case DELETE_TASK:
                    handleDeleteTask();
                    break;
                case UNKNOWN:
                    sendNotFound();
                    break;
            }
        } catch (NumberFormatException e) {
            sendText("parameter id is not a number", 406);
        } catch (IntersectionException e) {
            sendIntersection();
        } catch (NotFoundException e) {
            sendNotFound();
        }
    }

    abstract void handleGetAllTasks() throws IOException;

    abstract void handleGetTaskById() throws IOException, NotFoundException;

    abstract void handleCreateTask() throws IOException, IntersectionException, NotFoundException;

    abstract void handleUpdateTask() throws IOException, IntersectionException, NotFoundException;

    abstract void handleDeleteTask() throws IOException, NotFoundException;

    protected int getId() throws IOException {
        return Integer.parseInt(h.getRequestURI().getPath().split("/")[2]);
    }

    private Endpoint getEndpoint(String method, String pathString) {
        String[] path = pathString.split("/");

        if (method.equals("GET")) {
            if (path.length == 2) return Endpoint.GET_ALL_TASKS;
            if (path.length == 3) return Endpoint.GET_TASK_BY_ID;
        }
        if (method.equals("POST")) {
            if (path.length == 2) return Endpoint.CREATE_TASK;
            if (path.length == 3) return Endpoint.UPDATE_TASK;
        }
        if (method.equals("DELETE") && path.length == 3) return Endpoint.DELETE_TASK;

        return Endpoint.UNKNOWN;
    }

    private enum Endpoint {GET_ALL_TASKS, GET_TASK_BY_ID, CREATE_TASK, UPDATE_TASK, DELETE_TASK, UNKNOWN}
}
