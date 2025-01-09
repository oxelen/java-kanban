package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.*;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void shouldEqualsSubtasksIfIdsEquals() {
        Subtask sub1 = new Subtask(0, "1", "1", TaskStatus.NEW, 0);

        Subtask sub2 = new Subtask(0, "1", "1", TaskStatus.NEW, 0);

        Assertions.assertEquals(sub1, sub2);
    }

    @Test
    void subTaskCantBeHisOwnEpic() {
        Subtask sub = new Subtask(0,"1", "1", TaskStatus.NEW, 0);

        Assertions.assertEquals(-1, sub.getEpicId());
    }
}