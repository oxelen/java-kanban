package httpTests;

import com.google.gson.Gson;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import http.HttpTaskServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTask();
        server.startServer();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void testGetTask() throws IntersectionException, IOException, InterruptedException,  NotFoundException {
        Task task1 = new Task("task1", "task1", TaskStatus.NEW);
        Task task2 = new Task("task2", "task2", TaskStatus.NEW);
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getAllTask()), response.body());

        uri = URI.create("http://localhost:8080/tasks/0");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getTaskById(0)), response.body());
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("task",
                "description",
                TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 0, 0),
                Duration.ofHours(1));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> taskList = manager.getAllTask();

        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(1, taskList.size(), "Некорректное количество задач");
        assertEquals("task", taskList.getFirst().getName(), "Некорректное имя");
    }

    @Test
    public void testUpdateTask() throws IntersectionException, IOException, InterruptedException {
        Task task = new Task(0, "task", "task", TaskStatus.NEW);
        manager.addTask(task);

        task.setName("upd task");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/0");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Task> taskList = manager.getAllTask();

        assertEquals(task, taskList.getFirst());
    }

    @Test
    public void testDeleteTask() throws IntersectionException, IOException, InterruptedException {
        Task task = new Task(0, "task", "task", TaskStatus.NEW);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/0");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

         assertEquals(200, response.statusCode());
         List<Task> taskList = manager.getAllTask();

         assertTrue(taskList.isEmpty());
    }
}
