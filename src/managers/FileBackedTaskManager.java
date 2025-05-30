package managers;

import task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File autoSaveFile;

    public FileBackedTaskManager(File autoSaveFile) {
        super();
        this.autoSaveFile = autoSaveFile;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("src\\save\\autosave.csv");

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        Task task2 = new Task("task2", "d2", TaskStatus.NEW);
        Task task3 = new Task("task3", "d3", TaskStatus.NEW);
        manager.addTask(task2);
        manager.addTask(task3);
    }

    @Override
    public void addTask(Task... tasks) {
        super.addTask(tasks);
        save();
    }

    @Override
    public void addEpic(Epic... epics) {
        super.addEpic(epics);
        save();
    }

    @Override
    public void addSubtask(Subtask... subtasks) {
        super.addSubtask(subtasks);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);

        String fileString = Files.readString(file.toPath());
        if (fileString.isEmpty())
            return loadedManager;
        String[] strokes = fileString.split("\n");
        for (int i = 1; i < strokes.length; i++) {
            Task task = fromString(strokes[i]);

            if (task.getClass().equals(Epic.class)) {
                Epic epic = (Epic) task;
                loadedManager.addEpic(epic);
            } else if (task.getClass().equals(Subtask.class)) {
                Subtask sub = (Subtask) task;
                loadedManager.addSubtask(sub);
            } else
                loadedManager.addTask(task);
        }
        return loadedManager;
    }

    public File getAutoSaveFile() {
        return autoSaveFile;
    }

    private void save() {
        List<Task> taskListToSave = new ArrayList<>();
        if (!taskMap.isEmpty())
            taskListToSave.addAll(taskMap.values());
        if (!epicMap.isEmpty())
            taskListToSave.addAll(epicMap.values());
        if (!subtaskMap.isEmpty())
            taskListToSave.addAll(subtaskMap.values());

        try (Writer fileWriter = new FileWriter(autoSaveFile)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : taskListToSave) {
                fileWriter.write(toString(task) + "\n");
            }
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Ошибка сохранения.");
            } catch (ManagerSaveException managerExc) {
                System.out.println(managerExc.getMessage());
            }
        }
    }

    public static String toString(Task task) {
        String epicId = "";
        TaskType type;
        if (task.getClass().equals(Epic.class))
            type = TaskType.EPIC;
        else if (task.getClass().equals(Subtask.class))
            type = TaskType.SUBTASK;
        else
            type = TaskType.TASK;

        if (type.equals(TaskType.SUBTASK)) {
            Subtask sub = (Subtask) task;
            epicId = Integer.toString(sub.getEpicId());
        }

        return String.format("%s,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId);
    }

    private static Task fromString(String value) {
        String[] elements = value.split(",");
        int id = Integer.parseInt(elements[0]);
        String name = elements[2];
        String description = elements[4];
        TaskStatus status = getStatusFromString(elements[3]);
        int epicId = -1;

        if (elements[1].equals("SUBTASK"))
            epicId = Integer.parseInt(elements[5]);
        switch (elements[1]) {
            case "TASK":
                return new Task(id, name, description, status);
            case "EPIC":
                return new Epic(id, name, description);
            case "SUBTASK":
                return new Subtask(id, name, description, status, epicId);
            default:
                return null;
        }
    }

    private static TaskStatus getStatusFromString(String status) {
        switch (status) {
            case "NEW":
                return TaskStatus.NEW;
            case "IN_PROGRESS":
                return TaskStatus.IN_PROGRESS;
            case "DONE":
                return TaskStatus.DONE;
            default:
                return null;
        }
    }
}

class ManagerSaveException extends Exception {
    ManagerSaveException(String message) {
        super(message);
    }
}