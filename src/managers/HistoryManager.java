package managers;

import exceptions.NotFoundException;
import task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory() throws NotFoundException;

    void remove(int id);
}
