package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import task.*;

class TaskTest {
    @Test
    void shouldEqualsTasksIfIdsEquals() {
        Task task1 = new Task(0, "1", "1", TaskStatus.NEW);

        Task task2 = new Task(0, "1", "1", TaskStatus.NEW);

        Assertions.assertEquals(task1, task2);
    }
}