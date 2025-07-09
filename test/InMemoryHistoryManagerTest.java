import managers.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    private Task task1;
    private Task task2;

    private Task task3;

    HistoryManager history;

    @BeforeEach
    void init() {
        task1 = new Task(1,
                "1",
                "1",
                TaskStatus.NEW);
        task2 = new Task(2,
                "2",
                "2",
                TaskStatus.NEW);

        task3 = new Task(3,
                "3",
                "3",
                TaskStatus.NEW);

        history = Managers.getDefaultHistory();
    }

    @Test
    void shouldCreateEmptyHistory() {
        Assertions.assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void shouldNotAddSameTasks() {
        history.add(task1);
        history.add(task1);

        Assertions.assertEquals(1, history.getHistory().size());
    }

    @Test
    void shouldRemoveFirst() {
        List<Task> target = List.of(task2, task3);

        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task1.getId());

        assertEquals(target, history.getHistory());
    }

    @Test
    void shouldRemoveMiddle() {
        List<Task> target = List.of(task1, task3);

        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task2.getId());

        assertEquals(target, history.getHistory());
    }

    @Test
    void shouldRemoveLast() {
        List<Task> target = List.of(task1, task2);

        history.add(task1);
        history.add(task2);
        history.add(task3);

        history.remove(task3.getId());

        assertEquals(target, history.getHistory());
    }

    @Test
    void singleTaskHistoryWorksCorrect() {
        history.add(task1);
        assertEquals(List.of(task1), history.getHistory());
    }

    @Test
    void addElementsHistoryWorksCorrect() {
        history.add(task1);
        history.add(task2);
        history.add(task3);
        history.add(task1);

        assertEquals(List.of(task2, task3, task1), history.getHistory());
    }

    @Test
    void removedHistoryShouldBeEmpty() {
        history.add(task1);
        history.remove(task1.getId());

        Assertions.assertTrue(history.getHistory().isEmpty());
    }
}