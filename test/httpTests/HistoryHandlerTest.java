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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryHandlerTest {
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
    public void testGetHistory() throws IntersectionException, NotFoundException, IOException, InterruptedException {
        Task task = new Task(0, "task", "task", TaskStatus.NEW);
        manager.addTask(task);
        manager.getTaskById(0);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> history = manager.getHistory();

        assertEquals(gson.toJson(history), response.body());
    }
}
