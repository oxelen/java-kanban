package managers;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> taskMap;
    protected final HashMap<Integer, Epic> epicMap;
    protected final HashMap<Integer, Subtask> subtaskMap;

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.equals(task2)) return 0;
        if (task1.getStartTime().get().isBefore(task2.getStartTime().get()))
            return -1;
        else
            return 1;
    });

    private final HistoryManager history;

    private int idCreator;

    public InMemoryTaskManager() {
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
        Arrays.stream(tasks)
                .forEach(task -> {
                    if (task.getId() == -1)
                        task.setId(createId());
                    updateTask(task);
                });
    }

    @Override
    public void addEpic(Epic... epics) {
        Arrays.stream(epics)
                .forEach(epic -> {
                    if (epic.getId() == -1)
                        epic.setId(createId());
                    updateEpic(epic);
                });
    }

    @Override
    public void addSubtask(Subtask... subtasks) {
        Arrays.stream(subtasks)
                .forEach(subtask -> {
                    if (subtask.getId() == -1)
                        subtask.setId(createId());
                    epicMap.get(subtask.getEpicId())
                            .addSubtaskId(subtask.getId());
                    updateSubtask(subtask);
                });
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
        taskMap.keySet().forEach(history::remove);
        taskMap.clear();
    }

    @Override
    public void deleteAllEpic() {
        epicMap.keySet().forEach(history::remove);
        epicMap.clear();
        deleteAllSubtask();
    }

    @Override
    public void deleteAllSubtask() {
        subtaskMap.keySet().forEach(history::remove);
        subtaskMap.clear();

        epicMap.values().forEach(epic -> {
            epic.clearSubtaskList();
            updateEpicStatusAndTime(epic.getId());
        });
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
        if (isNotCrossWithTasks(task)) {
            taskMap.put(task.getId(), task);
            addToPrioritizedSet(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isNotCrossWithTasks(subtask)) {
            subtaskMap.put(subtask.getId(), subtask);
            updateEpicStatusAndTime(subtask.getEpicId());
            addToPrioritizedSet(subtask);
        }
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
        getSubtasksByEpicId(id)
                .forEach(sub -> deleteEpicById(sub.getId()));
        epicMap.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        history.remove(id);

        Epic tempEpic = epicMap.get(subtaskMap.get(id).getEpicId());
        subtaskMap.get(id).setId(-1);
        tempEpic.removeSubId(id);
        subtaskMap.remove(id);
    }

    //Get Subtasks by epic
    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        return epicMap.get(epicId).getSubtasks().stream()
                .map(subtaskMap::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return Objects.equals(taskMap, that.taskMap)
                && Objects.equals(epicMap, that.epicMap)
                && Objects.equals(subtaskMap, that.subtaskMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskMap, epicMap, subtaskMap);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void addToPrioritizedSet(Task task) {
        if (task.getStartTime().isPresent() && !prioritizedTasks.contains(task)) prioritizedTasks.add(task);
    }

    private boolean isNotCrossWithTasks(Task task) {
        return getPrioritizedTasks().stream()
                .filter(task::isCrossByTime)
                .toList()
                .isEmpty();
    }

    private void updateEpicStatusAndTime(int epicId) {
        Epic epic = epicMap.get(epicId);
        ArrayList<Subtask> subs = getSubtasksByEpicId(epicId);

        updateEpicStatus(epic, subs);
        updateEpicTime(epic, subs);

        addToPrioritizedSet(epic);
    }

    private void updateEpicStatus(Epic epic, ArrayList<Subtask> subs) {
        Set<TaskStatus> statuses = subs.stream()
                .map(Subtask::getStatus)
                .collect(Collectors.toSet());

        epic.updateStatus(statuses);
    }

    private void updateEpicTime(Epic epic, ArrayList<Subtask> subs) {
        LocalDateTime epicStartTime = subs.stream()
                .map(Task::getStartTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min((time1, time2) -> {
                    if (time1.isBefore(time2)) return -1;
                    else if (time1.isAfter(time2)) return 1;
                    else return 0;
                })
                .orElse(null);

        long totalSeconds = subs.stream()
                .map(Task::getDuration)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToLong(Duration::toSeconds)
                .sum();
        Duration epicDuration = Duration.ofSeconds(totalSeconds);

        if (epicStartTime != null && !epicDuration.isZero()) {
            epic.setStartTime(epicStartTime);
            epic.setDuration(epicDuration);
            epic.setEndTime(epicStartTime.plus(epicDuration));
        }
    }

    private int createId() {
        while (taskMap.containsKey(idCreator)
                || epicMap.containsKey(idCreator)
                || subtaskMap.containsKey(idCreator)) {
            idCreator++;
        }
        return idCreator;
    }
}