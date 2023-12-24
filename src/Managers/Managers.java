package Managers;

import java.io.File;

public class Managers {

    public static FileBackedTasksManager getDefault(File file) {
        return new FileBackedTasksManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
