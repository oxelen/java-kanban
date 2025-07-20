package httpTests;

import com.google.gson.Gson;
import exceptions.IntersectionException;
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
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedHandlerTest {
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
    public void testGetPrioritized() throws IntersectionException, IOException, InterruptedException {
        Task task1 = new Task(0,
                "task1",
                "task1",
                TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                Duration.ofHours(1));
        Task task2 = new Task(1,
                "task2",
                "task2",
                TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 2, 0),
                Duration.ofHours(1));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/prioritized");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        TreeSet<Task> taskList = manager.getPrioritizedTasks();
        assertEquals(gson.toJson(taskList), response.body());
    }
}
