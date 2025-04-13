package managers;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> taskMap;
    private final HashMap<Integer, Epic> epicMap;
    private final HashMap<Integer, Subtask> subtaskMap;

    private final HistoryManager history;

    private int idCreator;

    InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        history = Managers.getDefaultHistory();

        idCreator = 0;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    //Create Tasks
    @Override
    public void addTask(Task... tasks) {
        for (Task task : tasks) {
            task.setId(createId());
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void addEpic(Epic... epics) {
        for (Epic epic : epics) {
            epic.setId(createId());
            epicMap.put(epic.getId(), epic);
        }
    }

    @Override
    public void addSubtask(Subtask... subtasks) {
        for (Subtask subtask : subtasks) {
            subtask.setId(createId());
            subtaskMap.put(subtask.getId(), subtask);

            Epic tempEpic = epicMap.get(subtask.getEpicId());
            tempEpic.addSubtaskId(subtask.getId());

            updateEpicStatus(tempEpic.getId());
        }
    }

    //Get Tasks
    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskMap.values());
    }

    //Delete Task maps
    @Override
    public void deleteAllTask() {
        for (int id : taskMap.keySet()) {
            history.remove(id);
        }
        taskMap.clear();
    }

    @Override
    public void deleteAllEpic() {
        for (int id : epicMap.keySet()) {
            history.remove(id);
        }
        epicMap.clear();
        deleteAllSubtask();
    }

    @Override
    public void deleteAllSubtask() {
        for (int id : subtaskMap.keySet()) {
            history.remove(id);
        }
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.clearSubtaskList();
            updateEpicStatus(epic.getId());
        }
    }

    //Get tasks by id
    @Override
    public Task getTaskById(int id) {
        history.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        history.add(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        history.add(subtaskMap.get(id));
        return subtaskMap.get(id);
    }

    //Update Tasks
    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        for (Integer subId : epicMap.get(epic.getId()).getSubtasks()) {
            epic.addSubtaskId(subId);
        }
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    //Delete task by id
    @Override
    public void deleteTaskById(int id) {
        history.remove(id);
        taskMap.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        history.remove(id);
        for (Subtask sub : getSubtasksByEpicId(id)) {
            int subId = sub.getId();
            deleteSubtaskById(subId);
        }

        epicMap.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        history.remove(id);

        Epic tempEpic = epicMap.get(subtaskMap.get(id).getEpicId());
        tempEpic.removeSubId(id);
        subtaskMap.remove(id);
    }

    //Get Subtasks by epic
    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> res = new ArrayList<>();
        for (Integer subId : epicMap.get(epicId).getSubtasks()) {
            res.add(subtaskMap.get(subId));
        }
        return res;
    }


    private void updateEpicStatus(int epicId) {
        Epic epic = epicMap.get(epicId);
        ArrayList<Subtask> subs = getSubtasksByEpicId(epicId);
        Set<TaskStatus> statuses = new HashSet<>();

        for (Subtask sub : subs) {
            statuses.add(sub.getStatus());
        }

        epic.updateStatus(statuses);
    }

    private int createId() {
        return idCreator++;
    }
}