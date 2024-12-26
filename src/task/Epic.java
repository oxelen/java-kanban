package task;

import java.util.ArrayList;
import java.util.Set;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public void addSubtaskId(int subtaskId) {
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
        return "Epic{" +
                "epicSubTasks.length='" + subtasksId.size() + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
