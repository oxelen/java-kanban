package manager;

import java.util.*;

import task.*;

public class TaskManager {
    private final HashMap<Integer, Task> taskMap;
    private final HashMap<Integer, Epic> epicMap;
    private final HashMap<Integer, Subtask> subtaskMap;
    private int idCreator;

    public TaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();

        idCreator = 0;
    }

    private int createId() {
        return idCreator++;
    }

            //Create Tasks
    public void addTask(Task... tasks) {
        for(Task task : tasks) {
            task.setId(createId());
            taskMap.put(task.getId(), task);
        }
    }

    public void addEpic(Epic... epics) {
        for(Epic epic : epics) {
            epic.setId(createId());
            epicMap.put(epic.getId(), epic);
        }
    }

    public void addSubtask(Subtask... subtasks) {
        for(Subtask subtask : subtasks) {
            subtask.setId(createId());
            subtaskMap.put(subtask.getId(), subtask);
            getEpicById(subtask.getEpicId()).addSubtaskId(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
        }
    }

            //Get Tasks
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(taskMap.values());
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicMap.values());
    }

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskMap.values());
    }

            //Delete Task maps
    public void deleteAllTask() {
        taskMap.clear();
    }

    public void deleteAllEpic() {
        epicMap.clear();
        deleteAllSubtask();
    }

    public void deleteAllSubtask() {
        subtaskMap.clear();
    }

            //Get tasks by id
    public Task getTaskById(int id) {
        return taskMap.get(id);
    }

    public Epic getEpicById(int id) {
        return epicMap.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtaskMap.get(id);
    }

            //Update Tasks
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        for(Integer subId : getEpicById(epic.getId()).getSubtasks()) {
            epic.addSubtaskId(subId);
        }
        epicMap.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

            //Delete task by id
    public void deleteTaskById(int id) {
        taskMap.remove(id);
    }

    public void deleteEpicById(int id) {
        for(Subtask sub : getSubtasksByEpicId(id)) {
            deleteSubtaskById(sub.getId());
        }
        epicMap.remove(id);
    }

    public void deleteSubtaskById(int id) {
        subtaskMap.remove(id);
    }

            //Get Subtasks by epic
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> res = new ArrayList<>();
        for(Integer subId : getEpicById(epicId).getSubtasks()) {
            res.add(getSubtaskById(subId));
        }
        return res;
    }


    private void updateEpicStatus(int epicId) {
        Epic epic = getEpicById(epicId);
        ArrayList<Subtask> subs = getSubtasksByEpicId(epicId);
        Set<TaskStatus> statuses = new HashSet<>();

        for(Subtask sub : subs) {
            statuses.add(sub.getStatus());
        }

        epic.updateStatus(statuses);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TaskManager that = (TaskManager) o;
        return idCreator == that.idCreator && Objects.equals(taskMap, that.taskMap) && Objects.equals(epicMap, that.epicMap) && Objects.equals(subtaskMap, that.subtaskMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskMap, epicMap, subtaskMap, idCreator);
    }
}