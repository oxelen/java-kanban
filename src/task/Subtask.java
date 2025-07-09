package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name,
                   String description,
                   TaskStatus status,
                   int epicId) {
        super(name, description, status);
        setEpicId(epicId);
    }

    public Subtask(int id,
                   String name,
                   String description,
                   TaskStatus status,
                   int epicId) {
        super(id, name, description, status);
        setEpicId(epicId);
    }

    public Subtask(String name,
                   String description,
                   TaskStatus status,
                   int epicId,
                   LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        setEpicId(epicId);
    }

    public Subtask(int id,
                   String name,
                   String description,
                   TaskStatus status,
                   int epicId,
                   LocalDateTime startTime,
                   Duration duration) {
        super(id, name, description, status, startTime, duration);
        setEpicId(epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString()
                .replaceFirst("Task", "Subtask")
                .replaceFirst("}", ", " + "epicId='" + epicId + "'}");
    }

    private void setEpicId(int epicId) {
        if (epicId != this.id)
            this.epicId = epicId;
        else this.epicId = -1;
    }
}
