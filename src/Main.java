import Tasks.*;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("Task 1", "Single Task");
        Task task2 = new Task("Task 2", "Single Task");
        Epic epic3 = new Epic("Task 3", "Epic");
        SubTask sub4 = new SubTask("Task 4", "SubTask of Epic 1", epic3);
        SubTask sub5 = new SubTask("Task 5", "SubTask of Epic 1", epic3);
        Epic epic6 = new Epic("Task 6", "Epic");
        SubTask sub7 = new SubTask("Task 7", "SubTask of Epic 2", epic6);
        TaskManager.addTask(task1, task2, epic3, sub4, sub5, epic6, sub7);

        System.out.println("\tAll tasks:");
        print(TaskManager.getTaskList());
        System.out.println();

        System.out.println("\tAll single tasks:");
        print(TaskManager.getSingleTaskList());
        System.out.println();

        System.out.println("\tAll epics:");
        print(TaskManager.getEpicList());
        System.out.println();

        System.out.println("\tAll subTasks:");
        print(TaskManager.getSubTaskList());
        System.out.println();

        System.out.println("\tSubTasks of epic 1:");
        print(TaskManager.getEpicSubTasks(2));
        System.out.println();

        System.out.println("\tSubTasks of epic 2:");
        print(TaskManager.getEpicSubTasks(5));
        System.out.println();

        System.out.println("\tChange statuses: Task 1 (In progress), Task 2 (Done)");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);
        System.out.println(TaskManager.getTask(0));
        System.out.println(TaskManager.getTask(1));
        System.out.println();

        System.out.println("\tChange statuses: Task 4 (In progress), Task 5 (Done)");
        sub4.setStatus(TaskStatus.IN_PROGRESS);
        sub5.setStatus(TaskStatus.DONE);
        System.out.println(TaskManager.getTask(2));
        System.out.println(TaskManager.getTask(3));
        System.out.println(TaskManager.getTask(4));
        System.out.println();

        System.out.println("\tChange statuses: Task 4 (Done)");
        sub4.setStatus(TaskStatus.DONE);
        System.out.println(TaskManager.getTask(2));
        System.out.println(TaskManager.getTask(3));
        System.out.println(TaskManager.getTask(4));
        System.out.println();

        System.out.println("\tRemove task 1:");
        TaskManager.removeTask(0);
        print(TaskManager.getSingleTaskList());
        System.out.println();

        System.out.println("\tRemove epic:");
        TaskManager.removeTask(2);
        print(TaskManager.getTaskList());
        System.out.println();

        System.out.println("\tClear");
        TaskManager.clear();
        print(TaskManager.getTaskList());
    }

    static void print(HashMap map) {
        if(map.isEmpty()) {
            System.out.println("Empty!");
            return;
        }
        for(Object obj : map.values()) {
            System.out.println(obj);
        }
    }
}
