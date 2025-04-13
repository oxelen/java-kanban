package managers;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    void addTask(Task... tasks);

    void addEpic(Epic... epics);

    void addSubtask(Subtask... subtasks);

    //Get Tasks
    ArrayList<Task> getAllTask();

    ArrayList<Epic> getAllEpic();

    ArrayList<Subtask> getAllSubtask();

    //Delete Task maps
    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    //Get tasks by id
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    //Update Tasks
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    //Delete task by id
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    //Get Subtasks by epic
    ArrayList<Subtask> getSubtasksByEpicId(int epicId);
}
