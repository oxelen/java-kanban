package Tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static final HashMap<Integer, Task> taskList = new HashMap<>();

    public static void addTask(Task... tasks) {
        for(Task task : tasks) {
            taskList.put(task.getId(), task);
        }
    }

    public static void clear() {
        taskList.clear();
    }

    public static Task getTask(int id) {
        return taskList.get(id);
    }

    public static void removeTask(int id) {
        if(getTask(id) instanceof Epic) {
            ArrayList<SubTask> subs = ((Epic) getTask(id)).getEpicSubTasks();
            for(SubTask sub : subs) {
                removeTask(sub.getId());
            }
        }
        taskList.remove(id);
    }

    public static HashMap<Integer, SubTask> getEpicSubTasks(int id) {
        HashMap<Integer, SubTask> res = new HashMap<>();
        Epic epic = (Epic) getTask(id);
        for(SubTask sub : epic.getEpicSubTasks()) res.put(sub.getId(), sub);
        return res;
    }

        //Получение списков разных типов задач
    public static HashMap<Integer, Task> getSingleTaskList() {
        HashMap<Integer, Task> res = (HashMap<Integer, Task>) taskList.clone();
        for (Task task : taskList.values()) {
            if (task instanceof Epic || task instanceof SubTask) res.remove(task.getId());
        }
        return res;
    }

    public static HashMap<Integer, Epic> getEpicList() {
        HashMap<Integer, Epic> res = new HashMap<>();
        for (Task task : taskList.values())
            if (task instanceof Epic) res.put(task.getId(), (Epic) task);
        return res;
    }

    public static HashMap<Integer, SubTask> getSubTaskList() {
        HashMap<Integer, SubTask> res = new HashMap<>();
        for (Task task : taskList.values())
            if (task instanceof SubTask) res.put(task.getId(), (SubTask) task);
        return res;
    }

    public static HashMap<Integer, Task> getTaskList() {
        return taskList;
    }
}