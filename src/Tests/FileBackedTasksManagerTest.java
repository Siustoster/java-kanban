package Tests;

import Managers.FileBackedTasksManager;
import Managers.ManagerSaveException;
import Managers.Managers;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    final File testCsvFile = new File("src/Tests/", "TestCsv.csv");
    @BeforeEach
    @Override
    void createManager() {
        loadTestFile();
        taskManager = Managers.loadTaskManagerFromFile(testCsvFile);
    }
    private  void loadTestFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testCsvFile))) {

            writer.write("id,type,name,description,status,epic");

        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении данных в файл");
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void saveloadStateTestEmptyHistory() {
        Epic epic = new Epic("Эпик", "Эпикas");
        taskManager.createEpic(epic);

        FileBackedTasksManager  taskManager2 = FileBackedTasksManager.loadFromFile(testCsvFile);
        List<Epic> oldEpicList = taskManager.getAllEpics();
        List<Epic> newEpicList = taskManager2.getAllEpics();
        List<Task> historyOld = taskManager.getHistory();
        List<Task> historyNew = taskManager2.getHistory();
        assertEquals(oldEpicList,newEpicList);
        assertEquals(historyOld,historyNew);
    }

    @Test
    void saveloadStateTestNotEmptyHistory() {
        Epic epic = new Epic("Эпик", "Эпикas");
        taskManager.createEpic(epic);
        taskManager.getEpicById(1);
        FileBackedTasksManager  taskManager2 = FileBackedTasksManager.loadFromFile(testCsvFile);
        List<Epic> oldEpicList = taskManager.getAllEpics();
        List<Epic> newEpicList = taskManager2.getAllEpics();
        List<Task> historyOld = taskManager.getHistory();
        List<Task> historyNew = taskManager2.getHistory();
        assertEquals(oldEpicList,newEpicList);
        assertEquals(historyOld,historyNew);
    }


}