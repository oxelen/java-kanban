package managers;

import exceptions.NotFoundException;
import task.*;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory() throws NotFoundException;

    void remove(int id);
}
