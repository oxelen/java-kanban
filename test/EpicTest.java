import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;

import java.util.List;

class EpicTest {

    @Test
    void shouldEqualsEpicsIfIdsEquals() {
        Epic epic1 = new Epic(0, "1", "1");

        Epic epic2 = new Epic(0, "1", "1");

        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    void epicShouldNotBeAddedToHimself() {
        Epic epic = new Epic("1", "1");

        epic.addSubtaskId(epic.getId());
        List<Integer> subtasks = epic.getSubtasks();

        Assertions.assertTrue(subtasks.isEmpty());
    }
}