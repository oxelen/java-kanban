package managers;

import exceptions.IntersectionException;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import task.*;
import util.ManagerUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File autoSaveFile;

    public FileBackedTaskManager(File autoSaveFile) {
        super();
        this.autoSaveFile = autoSaveFile;
    }

    /*public static void main(String[] args) throws IOException {
        File autoSaveFile = new File("src\\save\\autosave.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(autoSaveFile);

        Task task1 = new Task("task1",
                "task1",
                TaskStatus.NEW,
                LocalDateTime.of(2025, 06, 20, 15, 0),
                Duration.ofHours(1));

        Task task2 = new Task("task2",
                "task2",
                TaskStatus.NEW,
                LocalDateTime.of(2025, 06, 29, 10, 0),
                Duration.ofHours(1));

        manager.addTask(task1, task2);

        Epic epic1 = new Epic("Epic", "epic");
        manager.addEpic(epic1);

        Subtask sub1 = new Subtask("sub1",
                "sub1",
                TaskStatus.NEW,
                epic1.getId(),
                LocalDateTime.of(2025, 6, 25, 20, 25),
                Duration.ofHours(4));
        Subtask sub2 = new Subtask("sub2",
                "sub2",
                TaskStatus.NEW,
                epic1.getId(),
                LocalDateTime.of(2025, 6, 25, 16, 25),
                Duration.ofHours(5));

        manager.addSubtask(sub1, sub2);

        Task taskWithoutTime = new Task("task", "task", TaskStatus.NEW);
        manager.addTask(taskWithoutTime);

        manager.getPrioritizedTasks().forEach(System.out::println);
        manager.getSubtasksByEpicId(2).forEach(System.out::println);
    }*/

    @Override
    public void addTask(Task task) throws IntersectionException {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) throws IntersectionException {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) throws IntersectionException, NotFoundException {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) throws IntersectionException, NotFoundException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws IntersectionException, NotFoundException {
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
    public void deleteTaskById(int id) throws NotFoundException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws NotFoundException {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) throws NotFoundException {
        super.deleteSubtaskById(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);

        String fileString = Files.readString(file.toPath());
        if (fileString.isEmpty())
            return loadedManager;

        Arrays.stream(fileString.split("\n")).skip(1).forEach(stroke -> {
            Task task = fromString(stroke);

            if (task.getClass().equals(Epic.class)) {
                Epic epic = (Epic) task;
                try {
                    loadedManager.addEpic(epic);
                } catch (IntersectionException e) {
                    throw new RuntimeException("load error");
                }
            } else if (task.getClass().equals(Subtask.class)) {
                Subtask sub = (Subtask) task;
                try {
                    loadedManager.addSubtask(sub);
                } catch (IntersectionException | NotFoundException e) {
                    throw new RuntimeException("load Error");
                }
            } else {
                try {
                    loadedManager.addTask(task);
                } catch (IntersectionException e) {
                    throw new RuntimeException("load Error");
                }
            }
        });
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
            fileWriter.write("id,type,name,status,description,startTime,duration,epic\n");
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

    private static String toString(Task task) {
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

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime()
                        .map(localDateTime -> localDateTime.format(ManagerUtil.FORMATTER)).orElse("null"),
                task.getDuration()
                        .map(duration -> String.valueOf(duration.toSeconds())).orElse("null"),
                epicId);
    }

    //Format of serialization: "id,type,name,status,description,startTime,duration,epicId"

    private static Task fromString(String value) {
        String[] elements = value.split(",");

        int id = Integer.parseInt(elements[0]);
        String name = elements[2];
        String description = elements[4];
        TaskStatus status = getStatusFromString(elements[3]);
        LocalDateTime startTime = null;
        Duration duration = null;

        if (!(elements[5].equals("null") && elements[6].equals("null"))) {
            startTime = LocalDateTime.parse(elements[5], ManagerUtil.FORMATTER);
            duration = Duration.ofSeconds(Long.parseLong(elements[6]));
        }

        return switch (elements[1]) {
            case "TASK" -> new Task(id, name, description, status, startTime, duration);
            case "EPIC" -> new Epic(id, name, description);
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(elements[7]);
                yield new Subtask(id, name, description, status, epicId, startTime, duration);
            }
            default -> null;
        };
    }

    private static TaskStatus getStatusFromString(String status) {
        return switch (status) {
            case "NEW" -> TaskStatus.NEW;
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "DONE" -> TaskStatus.DONE;
            default -> null;
        };
    }
}