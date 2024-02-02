package Tests;

import Managers.FileBackedTasksManager;
import Exceptions.ManagerSaveException;
import Managers.Managers;
import Tasks.Epic;
import Tasks.Statuses;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
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

            writer.write("id,type,name,description,status,epic,startTime,duration");

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
        LocalDateTime startTime = LocalDateTime.now();
        Epic epic = new Epic("Эпик", "Эпикas");
        taskManager.createEpic(epic);
        taskManager.getEpicById(1);
        Task task = new Task("Таск","task", Statuses.NEW,startTime,5);
        taskManager.createTask(task);
        Subtask subtask1 = new Subtask("Сабтаск","Сабтаск",Statuses.IN_PROGRESS,1
                ,startTime.plusMinutes(10),10);
        taskManager.createSubTask(subtask1);
        FileBackedTasksManager  taskManager2 = FileBackedTasksManager.loadFromFile(testCsvFile);
        List<Epic> oldEpicList = taskManager.getAllEpics();
        List<Epic> newEpicList = taskManager2.getAllEpics();
        List<Task> historyOld = taskManager.getHistory();
        List<Task> historyNew = taskManager2.getHistory();
        assertEquals(oldEpicList,newEpicList);
        assertEquals(historyOld,historyNew);
    }


}