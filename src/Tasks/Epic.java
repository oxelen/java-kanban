package Tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> epicSubTasks;

    public Epic(String name, String description) {
        super(name, description);
        epicSubTasks = new ArrayList<>();
    }

    void updateStatus() {
        boolean isNew = true;
        boolean isDone = true;
        for(SubTask sub : epicSubTasks) {
            if (sub.getStatus() != TaskStatus.NEW) isNew = false;
            if (sub.getStatus() != TaskStatus.DONE) isDone = false;
        }
        if (isNew) {
            status = TaskStatus.NEW;
            return;
        }
        if (isDone) {
            status = TaskStatus.DONE;
            return;
        }
        status = TaskStatus.IN_PROGRESS;
    }

    void addSubTask(SubTask subTask) {
        epicSubTasks.add(subTask);
    }

    public ArrayList<SubTask> getEpicSubTasks() {
        return epicSubTasks;
    }

    @Override
    public String toString() {
        String result = "Epic{";
        if (epicSubTasks == null) result += "epicSubTasks=null, ";
        else result += "epicSubTasks.length='" + epicSubTasks.size() + '\'';
        return result += ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public void setStatus(TaskStatus status) {

    }
}
