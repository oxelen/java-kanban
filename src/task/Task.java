package task;

import util.ManagerUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name,
                String description,
                TaskStatus status) {
        this.id = -1;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id,
                String name,
                String description,
                TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name,
                String description,
                TaskStatus status,
                LocalDateTime startTime,
                Duration duration) {
        this.id = -1;
        this.name = name;
        this.description = description;
        this.status = status;
        setStartTime(startTime);
        setDuration(duration);
    }

    public Task(int id,
                String name,
                String description,
                TaskStatus status,
                LocalDateTime startTime,
                Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        setStartTime(startTime);
        setDuration(duration);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (getStartTime().isPresent() && getDuration().isPresent()) {
            return Optional.of(getStartTime().get().plus(getDuration().get()));
        }

        return Optional.empty();
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public boolean isCrossByTime(Task task) {
        if (this.getEndTime().isEmpty() || task.getEndTime().isEmpty()) return false;

        LocalDateTime start1 = this.startTime;
        LocalDateTime end1 = this.getEndTime().get();

        LocalDateTime start2 = task.startTime;
        LocalDateTime end2 = task.getEndTime().get();

        return !start2.isAfter(end1) && !end2.isBefore(start1);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        String res = "Task={"
                + "id='" + id + "', "
                + "name='" + name + "', "
                + "description='" + description + "', "
                + "status='" + status;

        return (getEndTime().isPresent())
                ? (res + "', "
                + "startTime='" + startTime.format(ManagerUtil.FORMATTER) + "', "
                + "duration(in seconds)='" + duration.toSeconds() + "', "
                + "endTime='" + getEndTime().get().format(ManagerUtil.FORMATTER) + "'}")
                : (res + "'}");
    }
}
