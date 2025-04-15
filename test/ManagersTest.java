import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    void shouldBeInitializedManagers() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Assertions.assertNotNull(taskManager);
        Assertions.assertNotNull(historyManager);
    }
}