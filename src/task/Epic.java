package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public class Epic extends Task {
    private LocalDateTime endTime;
    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.id)
            subtasksId.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasksId;
    }

    public void updateStatus(Set<TaskStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            status = TaskStatus.NEW;
            return;
        }
        if (statuses.size() == 1) {
            if (statuses.contains(TaskStatus.NEW))
                status = TaskStatus.NEW;
            else if (statuses.contains(TaskStatus.DONE))
                status = TaskStatus.DONE;
        } else status = TaskStatus.IN_PROGRESS;
    }

    public void clearSubtaskList() {
        subtasksId.clear();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("Task", "Epic")
                .replaceFirst("}", ", " +
                        "epicSubTasks.length='" + subtasksId.size() + "'}");
    }

    public int removeSubId(Integer id) {
        subtasksId.remove(id);
        return id;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }
}
