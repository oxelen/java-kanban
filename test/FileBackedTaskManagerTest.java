import exceptions.IntersectionException;
import exceptions.NotFoundException;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private final String firstStrokeInFile = "id,type,name,status,description,startTime,duration,epic";
    private final Task task1 = new Task("task1",
            "description task1",
            TaskStatus.NEW);
    private final Epic epic1 = new Epic("epic1", "description epic1");
    private final Subtask sub1 = new Subtask("sub1",
            "description sub1",
            TaskStatus.NEW,
            1);
    private FileBackedTaskManager manager;
    File tempFile;

    @Override
    FileBackedTaskManager createManager() {
        tempFile = createTempFile();
        return new FileBackedTaskManager(tempFile);
    }

    @BeforeEach
    void initializeManager() {
        manager = createManager();
    }

    @Test
    void shouldSaveEmptyFile() throws IOException {
        File autoSaveFile = manager.getAutoSaveFile();
        Assertions.assertEquals("", Files.readString(autoSaveFile.toPath()));
    }

    @Test
    void shouldLoadEmptyFile() throws IOException {
        File autoSaveFile = manager.getAutoSaveFile();
        Assertions.assertEquals("", Files.readString(autoSaveFile.toPath()));
    }

    @Test
    void shouldSaveSomeTasks() throws IOException, IntersectionException, NotFoundException {
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(sub1);
        File autoSaveFile = manager.getAutoSaveFile();

        String targetString = String.format(firstStrokeInFile
                + "\n0,TASK,task1,NEW,description task1,null,null,"
                + "\n1,EPIC,epic1,NEW,description epic1,null,null,"
                + "\n2,SUBTASK,sub1,NEW,description sub1,null,null,1\n");

        Assertions.assertEquals(targetString, Files.readString(autoSaveFile.toPath()));
    }

    @Test
    void shouldLoadSomeTasks() throws IOException, IntersectionException, NotFoundException {
        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(sub1);

        try (Writer fileWriter = new FileWriter(tempFile)) {
            String testString = String.format(firstStrokeInFile
                    + "\n0,TASK,task1,NEW,description task1,null,null,"
                    + "\n1,EPIC,epic1,NEW,description epic1,null,null,"
                    + "\n2,SUBTASK,sub1,NEW,description sub1,null,null,1\n");
            fileWriter.write(testString);
        }

        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertEquals(manager, testManager);
    }

    @Test
    void shouldNotThrowExceptionWhenSaveFile() {
        Assertions.assertDoesNotThrow(() -> {
            manager.addTask(task1);
            manager.addEpic(epic1);
            manager.addSubtask(sub1);
        }, "Сохранение файлов не должно вызывать исключение");
    }

    private File createTempFile() {
        try {
            return File.createTempFile("temp", ".csv");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
