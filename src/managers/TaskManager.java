package managers;

import exceptions.IntersectionException;
import exceptions.NotFoundException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    List<Task> getHistory() throws NotFoundException;

    //Create Tasks
    void addTask(Task task) throws IntersectionException;

    void addEpic(Epic epic) throws IntersectionException;

    void addSubtask(Subtask subtask) throws IntersectionException, NotFoundException;

    //Get Tasks
    ArrayList<Task> getAllTask();

    ArrayList<Epic> getAllEpic();

    ArrayList<Subtask> getAllSubtask();

    //Delete Task maps
    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    //Get tasks by id
    Task getTaskById(int id) throws NotFoundException;

    Epic getEpicById(int id) throws NotFoundException;

    Subtask getSubtaskById(int id) throws NotFoundException;

    //Update Tasks
    void updateTask(Task task) throws IntersectionException, NotFoundException;

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask) throws IntersectionException, NotFoundException;

    //Delete task by id
    void deleteTaskById(int id) throws NotFoundException;

    void deleteEpicById(int id) throws NotFoundException;

    void deleteSubtaskById(int id) throws NotFoundException;

    ArrayList<Subtask> getSubtasksByEpicId(int epicId) throws NotFoundException;

    TreeSet<Task> getPrioritizedTasks();
}
