package managers;

import exceptions.IntersectionException;
import exceptions.NotFoundException;
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

    /*protected final TreeSet<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.equals(task2)) return 0;
        if (task1.getStartTime().get().isBefore(task2.getStartTime().get()))
            return -1;
        else
            return 1;
    });*/

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
    public List<Task> getHistory() throws NotFoundException {
        return history.getHistory();
    }

    //Create Tasks
    @Override
    public void addTask(Task task) throws IntersectionException {
        if (taskMap.containsKey(task.getId()) || isCrossWithTasks(task)) throw new IntersectionException();
        if (task.getId() == -1)
            task.setId(createId());
        taskMap.put(task.getId(), task);
        addToPrioritizedSet(task);
    }

    @Override
    public void addEpic(Epic epic) throws IntersectionException {
        if (epicMap.containsKey(epic.getId()) || isCrossWithTasks(epic)) throw new IntersectionException();
        if (epic.getId() == -1)
            epic.setId(createId());
        epicMap.put(epic.getId(), epic);
        addToPrioritizedSet(epic);
    }

    @Override
    public void addSubtask(Subtask subtask) throws IntersectionException, NotFoundException {
        if (subtaskMap.containsKey(subtask.getId()) || isCrossWithTasks(subtask)) throw new IntersectionException();
        if (subtask.getId() == -1)
            subtask.setId(createId());
        if (!epicMap.containsKey(subtask.getEpicId())) throw new NotFoundException();
        epicMap.get(subtask.getEpicId())
                .addSubtaskId(subtask.getId());

        subtaskMap.put(subtask.getId(), subtask);
        updateEpicStatusAndTime(subtask.getEpicId());
        addToPrioritizedSet(subtask);
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
    public Task getTaskById(int id) throws NotFoundException {
        if (!taskMap.containsKey(id)) throw new NotFoundException();
        history.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public Epic getEpicById(int id) throws NotFoundException {
        if (!epicMap.containsKey(id)) throw new NotFoundException();
        history.add(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) throws NotFoundException {
        if (!subtaskMap.containsKey(id)) throw new NotFoundException();
        history.add(subtaskMap.get(id));
        return subtaskMap.get(id);
    }

    //Update Tasks
    @Override
    public void updateTask(Task task) throws IntersectionException, NotFoundException {
        int id = task.getId();
        if (!taskMap.containsKey(id)) throw new NotFoundException();
        prioritizedTasks.remove(taskMap.get(id));
        taskMap.remove(id);

        addTask(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) throws IntersectionException, NotFoundException {
        int id = subtask.getId();
        if (!subtaskMap.containsKey(id)) throw new NotFoundException();
        prioritizedTasks.remove(subtaskMap.get(id));
        epicMap.get(subtaskMap.get(id).getEpicId())
                .removeSubId(id);
        updateEpicStatusAndTime((subtaskMap.get(id).getEpicId()));
        subtaskMap.remove(id);

        addSubtask(subtask);
    }

    //Delete task by id
    @Override
    public void deleteTaskById(int id) throws NotFoundException {
        if (!taskMap.containsKey(id)) throw new NotFoundException();
        history.remove(id);
        prioritizedTasks.remove(getTaskById(id));
        taskMap.remove(id);
    }

    @Override
    public void deleteEpicById(int id) throws NotFoundException {
        if (!epicMap.containsKey(id)) throw new NotFoundException();
        history.remove(id);
        getSubtasksByEpicId(id)
                .forEach(sub -> {
                    try {
                        deleteSubtaskById(sub.getId());
                    } catch (NotFoundException e) {
                        throw new RuntimeException("error");
                    }
                });
        prioritizedTasks.remove(epicMap.get(id));
        epicMap.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) throws NotFoundException {
        if (!subtaskMap.containsKey(id)) throw new NotFoundException();
        history.remove(id);
        prioritizedTasks.remove(subtaskMap.get(id));
        epicMap.get(subtaskMap.get(id).getEpicId())
                .removeSubId(id);
        subtaskMap.remove(id);
    }

    //Get Subtasks by epic
    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) throws NotFoundException {
        if (!epicMap.containsKey(epicId)) throw new NotFoundException();
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
        if (task.getStartTime().isPresent()) prioritizedTasks.add(task);
    }

    private boolean isCrossWithTasks(Task task) {
        return !getPrioritizedTasks().stream()
                .filter(task::isCrossByTime)
                .toList()
                .isEmpty();
    }

    private void updateEpicStatusAndTime(int epicId) {
        Epic epic = epicMap.get(epicId);
        try {
            ArrayList<Subtask> subs = getSubtasksByEpicId(epicId);
            updateEpicStatus(epic, subs);
            updateEpicTime(epic, subs);

            addToPrioritizedSet(epic);
        } catch (NotFoundException e) {
            throw new RuntimeException("error");
        }
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