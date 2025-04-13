import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void shouldAddDifferentTasks() {
        Task task = new Task(0,"1", "1", TaskStatus.NEW);
        manager.addTask(task);
        Epic epic = new Epic(1, "2", "2");
        manager.addEpic(epic);
        Subtask sub = new Subtask(2, "3", "3", TaskStatus.NEW, 1);
        manager.addSubtask(sub);

        assertInstanceOf(Task.class, manager.getTaskById(0));
        assertInstanceOf(Epic.class, manager.getEpicById(1));
        assertInstanceOf(Subtask.class, manager.getSubtaskById(2));
    }

    @Test
    void shouldNotConflictGeneratedIdAndGiven() {
        Task task1 = new Task("1", "1", TaskStatus.NEW);
        manager.addTask(task1);

        Task task2 = new Task(0, "2", "2", TaskStatus.NEW);
        manager.addTask(task2);

        assertNotEquals(task2, manager.getTaskById(0));
    }

    @Test
    void taskShouldNotChangeWhenAdded() {
        Task task = new Task("1", "1", TaskStatus.NEW);
        manager.addTask(task);

        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    void deletedSubtaskShouldNotKeepIdAfterDelete() {
        Epic epic = new Epic(0, "0", "0");
        Subtask sub = new Subtask(1, "1", "1", TaskStatus.NEW, 0);
        manager.addEpic(epic);
        manager.addSubtask(sub);

        manager.deleteSubtaskById(sub.getId());

        assertNotEquals(1, sub.getId());
    }

    @Test
    void epicsShouldNotKeepDeletedSubtaskId() {
        Epic epic = new Epic(0, "0", "0");
        Subtask sub = new Subtask(1, "1", "1", TaskStatus.NEW, 0);
        manager.addEpic(epic);
        manager.addSubtask(sub);

        manager.deleteSubtaskById(1);

        assertFalse(epic.getSubtasks().contains(1));
    }
}