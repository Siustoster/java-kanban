package Managers;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {
    @Override
    @BeforeEach
    void createManager() {
        taskManager = Managers.getDefault();
    }

}