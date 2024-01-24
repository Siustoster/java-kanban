package Managers;

import java.io.File;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static FileBackedTasksManager loadTaskManagerFromFile(File fileName) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(fileName);
        fileBackedTasksManager.loadFromFile(fileName);

        return fileBackedTasksManager;
    }
}
