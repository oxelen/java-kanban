import managers.*;

import java.util.List;
import task.*;

public class Main {

    /*public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("task1", "1", TaskStatus.NEW);
        Task task2 = new Task("task2", "2", TaskStatus.NEW);
        manager.addTask(task1, task2);

        Epic epic1 = new Epic("epic1", "1");
        Epic epic2 = new Epic("epic2", "2");
        manager.addEpic(epic1, epic2);

        Subtask sub1 = new Subtask("sub1", "epic1", TaskStatus.NEW, epic1.getId());
        Subtask sub2 = new Subtask("sub2", "epic1", TaskStatus.NEW, epic1.getId());
        Subtask sub3 = new Subtask("sub3", "epic1", TaskStatus.NEW, epic1.getId());
        manager.addSubtask(sub1, sub2, sub3);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());

        System.out.println("\tHistory:");
        printList(manager.getHistory());

        manager.getSubtaskById(sub1.getId());
        manager.getSubtaskById(sub2.getId());
        manager.getSubtaskById(sub3.getId());
        manager.getTaskById(task1.getId());

        System.out.println("\tHistory:");
        printList(manager.getHistory());

        manager.deleteEpicById(epic2.getId());

        System.out.println("\tHistory:");
        printList(manager.getHistory());

        manager.deleteEpicById(epic1.getId());

        System.out.println("\tHistory:");
        printList(manager.getHistory());
    }*/

    static <T> void printList(List<T> list) {
        for (T item : list) {
            System.out.println(item);
        }
    }
}
