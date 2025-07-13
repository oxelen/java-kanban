import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {
    Task task1;
    Task task2;

    @BeforeEach
    void initTasks() {
        task1 = new Task(0, "1", "1", TaskStatus.NEW);
        task2 = new Task(0, "1", "1", TaskStatus.NEW);
    }

    @Test
    void shouldEqualsTasksIfIdsEquals() {
        Assertions.assertEquals(task1, task2);
    }

    @Test
    void shouldGetRightEndTime() {
        task1.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 0));
        task1.setDuration(Duration.ofHours(1));

        Assertions.assertEquals(task1.getEndTime().get(),
                LocalDateTime.of(2000, 1, 1, 2, 0));
    }

    @Test
    void shouldCrossWhenTimeIsSame() {
        task1.setStartTime(LocalDateTime.of(2000, 1, 1, 0, 0));
        task1.setDuration(Duration.ofHours(1));

        task2.setStartTime(LocalDateTime.of(2000, 1, 1, 0, 0));
        task2.setDuration(Duration.ofHours(1));

        Assertions.assertTrue(task1.isCrossByTime(task2));
    }

    @Test
    void shouldCrossWhenFirstIntervalInsideSecond() {
        task1.setStartTime(LocalDateTime.of(2000,1,1,0,0));
        task1.setDuration(Duration.ofHours(3));

        task2.setStartTime(LocalDateTime.of(2000,1,1,1,0));
        task2.setDuration(Duration.ofHours(1));

        Assertions.assertTrue(task1.isCrossByTime(task2));
    }

    @Test
    void shouldCrossWhenFirstStartBeforeSecondEndBeforeFirstEnd() {
        task1.setStartTime(LocalDateTime.of(2000, 1, 1, 0, 0));
        task1.setDuration(Duration.ofHours(3));

        task2.setStartTime(LocalDateTime.of(2000, 1, 1, 1, 0));
        task2.setDuration(Duration.ofHours(5));

        Assertions.assertTrue(task1.isCrossByTime(task2));
    }

    @Test
    void shouldCrossWhenFirstStartEqualsSecondEnd() {
        task1.setStartTime(LocalDateTime.of(2000, 1, 1, 0, 0));
        task1.setDuration(Duration.ofHours(3));

        task2.setStartTime(LocalDateTime.of(2000, 1, 1, 3, 0));
        task2.setDuration(Duration.ofHours(3));

        Assertions.assertTrue(task1.isCrossByTime(task2));
    }

    @Test
    void shouldNotCrossWhenFirstStartIsAfterSecondEnd() {
        task1.setStartTime(LocalDateTime.of(2000, 1, 1, 0, 0));
        task1.setDuration(Duration.ofHours(3));

        task2.setStartTime(LocalDateTime.of(2000, 1, 1, 4, 0));
        task2.setDuration(Duration.ofHours(3));

        Assertions.assertFalse(task1.isCrossByTime(task2));
    }
}