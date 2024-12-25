import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Task 1", "Single Task 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Single Task 2", TaskStatus.NEW);
        manager.addTask(task1, task2);
        assert (task1.equals(manager.getTaskById(task1.getId())));
        System.out.println("Added task");

        Epic epic1 = new Epic("Epic 1", "Epic 1");
        manager.addEpic(epic1);
        assert (epic1.equals(manager.getEpicById(epic1.getId())));
        System.out.println("Added epic");

        Subtask sub1 = new Subtask("Sub 1", "Sub 1 of Epic 1", TaskStatus.NEW, epic1.getId());
        Subtask sub2 = new Subtask("Sub 2", "Sub 2 of Epic 1", TaskStatus.NEW, epic1.getId());
        manager.addSubtask(sub1, sub2);
        assert (sub1.equals(manager.getSubtaskById(sub1.getId())));
        System.out.println("Added sub");

        Epic epic2 = new Epic("Epic 2", "Epic 2");
        manager.addEpic(epic2);

        Subtask sub3 = new Subtask("Sub 3", "Sub 3 of Epic 2", TaskStatus.NEW, epic2.getId());
        manager.addSubtask(sub3);

        System.out.println();

        printList(manager.getAllTask());
        System.out.println();
        printList(manager.getAllEpic());
        System.out.println();
        printList(manager.getAllSubtask());
        System.out.println();

        printList(manager.getSubtasksByEpicId(epic1.getId()));

        task1 = new Task(task1.getId(), "Task 1", "Updated Task 1", TaskStatus.DONE);
        manager.updateTask(task1);
        assert (task1.equals(manager.getTaskById(task1.getId())));
        System.out.println("Task updated");

        manager.deleteAllTask();
        assert (manager.getAllTask().isEmpty());
        System.out.println("Tasks deleted");

        sub1 = new Subtask(sub1.getId(), "Sub 1", "Updated Sub 1",
                TaskStatus.DONE, epic1.getId());
        manager.updateSubtask(sub1);

        System.out.println(manager.getEpicById(epic1.getId()));
        printList(manager.getSubtasksByEpicId(epic1.getId()));
        System.out.println();

        sub2 = new Subtask(sub2.getId(), "Sub 2", "Updated Sub 2",
                TaskStatus.DONE, epic1.getId());
        manager.updateSubtask(sub2);

        System.out.println(manager.getEpicById(epic1.getId()));
        printList(manager.getSubtasksByEpicId(epic1.getId()));
        System.out.println();

        Subtask sub4 = new Subtask("Sub 4", "Sub 4 of epic 1", TaskStatus.IN_PROGRESS, epic1.getId());
        manager.addSubtask(sub4);

        System.out.println(manager.getEpicById(epic1.getId()));
        printList(manager.getSubtasksByEpicId(epic1.getId()));


        manager.deleteAllEpic();
        printList(manager.getAllEpic());
        printList(manager.getAllSubtask());
    }

    static void printList(List list) {
        for (Object obj : list) {
            System.out.println(obj);
        }
    }
}
