import managers.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    Task task1 = new Task(1, "1", "1", TaskStatus.NEW);
    Task task2 = new Task(2, "2", "2", TaskStatus.NEW);

    Task task3 = new Task(3, "3", "3", TaskStatus.NEW);

    HistoryManager history = Managers.getDefaultHistory();


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

        assertEquals(new ArrayList<>(), history.getHistory());
    }

    /*@Test
    void canNotBeMoreThan10Elements() {
        HistoryManager history = Managers.getDefaultHistory();
        Task task = new Task("1", "2",  TaskStatus.NEW);

        for (int i = 0; i < 11; i++) {
            history.add(task);
        }

        List<Task> tasks = history.getHistory();

        assertEquals(10, tasks.size());
    }*/
}