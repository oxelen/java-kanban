package Tasks;

public class SubTask extends Task {
    private final Epic epic;

    public SubTask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        epic.addSubTask(this);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epic.getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        epic.updateStatus();
    }
}
