import exceptions.IntersectionException;
import exceptions.NotFoundException;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();

    }

    @Test
    void shouldAddDifferentTasks() throws NotFoundException, IntersectionException {
        Task task = new Task(0, "1", "1", TaskStatus.NEW);
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
    void shouldNotConflictGeneratedIdAndGiven() throws NotFoundException, IntersectionException {
        Task task1 = new Task(0, "1", "1", TaskStatus.NEW);
        manager.addTask(task1);

        Task task2 = new Task("2", "2", TaskStatus.NEW);
        manager.addTask(task2);

        assertNotEquals(task2, manager.getTaskById(0));
    }

    @Test
    void taskShouldNotChangeWhenAdded() throws NotFoundException, IntersectionException {
        Task task = new Task("1", "1", TaskStatus.NEW);
        manager.addTask(task);

        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    void epicsShouldNotKeepDeletedSubtaskId() throws IntersectionException, NotFoundException {
        Epic epic = new Epic(0, "0", "0");
        Subtask sub = new Subtask(1, "1", "1", TaskStatus.NEW, 0);
        manager.addEpic(epic);
        manager.addSubtask(sub);

        manager.deleteSubtaskById(1);

        assertFalse(epic.getSubtasks().contains(1));
    }

    @Test
    void historyShouldSavePreviousVersion() throws NotFoundException, IntersectionException {
        Task task1 = new Task("1", "1", TaskStatus.NEW);
        manager.addTask(task1);
        manager.getTaskById(0);

        Task task2 = new Task(0, "2", "2", TaskStatus.NEW);
        manager.updateTask(task2);
        manager.getTaskById(0);

        List<Task> history = manager.getHistory();

        assertEquals(task2, history.get(0));
    }

    @Test
    void shouldCalculateRightEpicTime() throws IntersectionException, NotFoundException {
        TaskManager manager = Managers.getDefault();

        Epic epic = new Epic(0, "epic", "epic");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask(1, "sub1", "sub1", TaskStatus.NEW, 0, LocalDateTime.of(2025, 6, 28, 18, 0), Duration.ofHours(5));
        Subtask sub2 = new Subtask(2, "sub2", "sub2", TaskStatus.NEW, 0, LocalDateTime.of(2025, 6, 29, 0, 0), Duration.ofHours(1));

        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        Assertions.assertEquals(epic.getStartTime().get(), LocalDateTime.of(2025, 6, 28, 18, 0));
        Assertions.assertEquals(epic.getDuration().get(), Duration.ofHours(6));
        Assertions.assertEquals(epic.getEndTime().get(), LocalDateTime.of(2025, 6, 29, 0, 0));
    }

    @Test
    void subtaskHasConnectedEpic() throws IntersectionException, NotFoundException {
        Epic epic = new Epic(0, "epic", "epic");
        Subtask sub = new Subtask(1, "sub", "sub", TaskStatus.NEW, 0);

        manager.addEpic(epic);
        manager.addSubtask(sub);

        Assertions.assertEquals(new ArrayList<Subtask>(List.of(sub)), manager.getSubtasksByEpicId(0));
    }

    @Test
    void shouldCalculateEpicStatus() throws IntersectionException, NotFoundException {
        Epic epic = new Epic(0, "epic", "epic");
        manager.addEpic(epic);
        Subtask sub1 = new Subtask(1, "sub1", "sub1", TaskStatus.NEW, 0);
        Subtask sub2 = new Subtask(2, "sub2", "sub2", TaskStatus.NEW, 0);
        Subtask sub3 = new Subtask(3, "sub3", "sub3", TaskStatus.NEW, 0);
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);
        manager.addSubtask(sub3);

        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());

        sub2 = new Subtask(2, "sub2", "sub2", TaskStatus.IN_PROGRESS, 0);
        sub3 = new Subtask(3, "sub3", "sub3", TaskStatus.DONE, 0);
        manager.updateSubtask(sub2);
        manager.updateSubtask(sub3);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        sub1 = new Subtask(1, "sub1", "sub1", TaskStatus.DONE, 0);
        sub2 = new Subtask(2, "sub2", "sub2", TaskStatus.DONE, 0);
        sub3 = new Subtask(3, "sub3", "sub3", TaskStatus.DONE, 0);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);
        manager.updateSubtask(sub3);

        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }
}
