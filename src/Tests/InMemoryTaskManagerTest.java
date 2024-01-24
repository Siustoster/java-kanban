package Tests;

import Managers.InMemoryTaskManager;
import Managers.Managers;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    @BeforeEach
    void createManager() {
        taskManager = Managers.getDefault();
    }

}