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
    protected Optional<Duration> duration = Optional.empty();
    protected Optional<LocalDateTime> startTime = Optional.empty();

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

    @Override
    public String toString() {
        String res = "Task={"
                + "id='" + id + "', "
                + "name='" + name + "', "
                + "description='" + description + "', "
                + "status='" + status;

        return (getEndTime().isPresent())
                ? (res + "', "
                + "startTime='" + startTime.get().format(ManagerUtil.FORMATTER) + "', "
                + "duration(in seconds)='" + duration.get().toSeconds() + "', "
                + "endTime='" + getEndTime().get().format(ManagerUtil.FORMATTER) + "'}")
                : (res + "'}");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (getStartTime().isPresent() && getDuration().isPresent()) {
            return Optional.of(getStartTime().get().plus(getDuration().get()));
        }

        return Optional.empty();
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public Optional<Duration> getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = Optional.ofNullable(startTime);
    }

    public void setDuration(Duration duration) {
        this.duration = Optional.ofNullable(duration);
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

    public boolean isCrossByTime(Task task) {
        if (this.getStartTime().isEmpty()) return false;

        LocalDateTime start1 = this.getStartTime().get();
        LocalDateTime end1 = this.getEndTime().get();

        LocalDateTime start2 = task.getStartTime().get();
        LocalDateTime end2 = task.getEndTime().get();

        return !start2.isAfter(end1) && !end2.isBefore(start1);
    }
}
