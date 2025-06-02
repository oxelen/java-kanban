import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Assertions;
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

public class SaveTest {
    private final String firstStrokeInFile = "id,type,name,status,description,epic";
    private final Task task1 = new Task("task1", "description task1", TaskStatus.NEW);
    private final Epic epic1 = new Epic("epic1", "description epic1");
    private final Subtask sub1 = new Subtask("sub1", "description sub1", TaskStatus.NEW, 1);

    @Test
    void shoudSaveEmptyFile() throws IOException {
        File tempFile = File.createTempFile("temp", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        File autoSaveFile = manager.getAutoSaveFile();
        Assertions.assertEquals("", Files.readString(autoSaveFile.toPath()));
    }

    @Test
    void shouldLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("temp", ".csv");
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

        File autoSaveFile = manager.getAutoSaveFile();
        Assertions.assertEquals("", Files.readString(autoSaveFile.toPath()));
    }

    @Test
    void shouldSaveSomeTasks() throws IOException {
        File tempFile = File.createTempFile("temp", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(sub1);
        File autoSaveFile = manager.getAutoSaveFile();

        String targetString = String.format(firstStrokeInFile
                + "\n0,TASK,task1,NEW,description task1,"
                + "\n1,EPIC,epic1,NEW,description epic1,"
                + "\n2,SUBTASK,sub1,NEW,description sub1,1\n");

        Assertions.assertEquals(targetString, Files.readString(autoSaveFile.toPath()));
    }

    @Test
    void shoudLoadSomeTasks() throws IOException {
        File tempFile = File.createTempFile("temp", ".csv");

        FileBackedTaskManager targetManager = new FileBackedTaskManager(tempFile);
        targetManager.addTask(task1);
        targetManager.addEpic(epic1);
        targetManager.addSubtask(sub1);

        try (Writer fileWriter = new FileWriter(tempFile)) {
            String testString = String.format(firstStrokeInFile
                    + "\n0,TASK,task1,NEW,description task1,"
                    + "\n1,EPIC,epic1,NEW,description epic1,"
                    + "\n2,SUBTASK,sub1,NEW,description sub1,1\n");
            fileWriter.write(testString);
        }

        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertEquals(targetManager, testManager);
    }
}
