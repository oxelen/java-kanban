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
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllEpic();
        server.startServer();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void testGetTask() throws IntersectionException, NotFoundException, IOException, InterruptedException {
        Epic epic1 = new Epic(0, "epic1", "epic1");
        Epic epic2 = new Epic(1, "epic2", "epic2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getAllEpic()), response.body());

        uri = URI.create("http://localhost:8080/epics/0");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getEpicById(0)), response.body());
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "epic", "epic");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> taskList = manager.getAllEpic();

        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(1, taskList.size(), "Некорректное количество задач");
        assertEquals("epic", taskList.getFirst().getName(), "Некорректное имя");
    }

    @Test
    public void testDeleteTasK() throws IntersectionException, IOException, InterruptedException {
        Epic epic = new Epic(0, "epic", "epic");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/0");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Epic> taskList = manager.getAllEpic();

        assertTrue(taskList.isEmpty());
    }

    @Test
    public void testGetEpicSubtasks() throws IntersectionException, NotFoundException, IOException, InterruptedException {
        Epic epic = new Epic(0, "epic", "epic");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask(1, "sub1", "sub1", TaskStatus.NEW, 0);
        Subtask sub2 = new Subtask(2, "sub2", "sub2", TaskStatus.NEW, 0);
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/0/subtasks");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subList = manager.getSubtasksByEpicId(0);
        assertEquals(gson.toJson(subList), response.body());
    }
}
