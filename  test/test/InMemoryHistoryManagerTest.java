package test;

import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void canNotBeMoreThan10Elements() {
        HistoryManager history = Managers.getDefaultHistory();
        Task task = new Task("1", "2",  TaskStatus.NEW);

        for (int i = 0; i < 11; i++) {
            history.add(task);
        }

        List<Task> tasks = history.getHistory();

        assertEquals(10, tasks.size());
    }
}