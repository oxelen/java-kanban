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
import task.Epic;
import task.Subtask;
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

public class SubtaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException, IntersectionException {
        manager.deleteAllEpic();
        manager.deleteAllSubtask();
        manager.addEpic(new Epic(0, "epic", "epic"));
        server.startServer();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException, IntersectionException, NotFoundException {
        Subtask sub1 = new Subtask("sub1", "sub1", TaskStatus.NEW, 0);
        Subtask sub2 = new Subtask("sub2", "sub2", TaskStatus.NEW, 0);
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getAllSubtask()), response.body());

        uri = URI.create("http://localhost:8080/subtasks/1");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(sub1), response.body());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Subtask sub = new Subtask("sub",
                "sub",
                TaskStatus.NEW,
                0,
                LocalDateTime.of(2000, 1, 1, 0, 0),
                Duration.ofHours(1));
        String taskJson = gson.toJson(sub);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> taskList = manager.getAllSubtask();

        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(1, taskList.size(), "Некорректное количество задач");
        assertEquals("sub", taskList.getFirst().getName(), "Некорректное имя");
    }

    @Test
    public void testUpdateSubtask() throws IntersectionException, IOException, InterruptedException, NotFoundException {
        Subtask sub = new Subtask(1, "sub", "sub", TaskStatus.NEW, 0);
        manager.addSubtask(sub);

        sub.setName("upd sub");
        String taskJson = gson.toJson(sub);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Subtask> taskList = manager.getAllSubtask();

        assertEquals(sub, taskList.getFirst());
    }

    @Test
    public void testDeleteTask() throws IntersectionException, NotFoundException, IOException, InterruptedException {
        Subtask sub = new Subtask(1, "sub", "sub", TaskStatus.NEW, 0);
        manager.addSubtask(sub);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> taskList = manager.getAllSubtask();

        assertTrue(taskList.isEmpty());
    }
}
