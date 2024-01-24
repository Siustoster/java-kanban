package Tests;

import Managers.FileBackedTasksManager;
import Managers.ManagerSaveException;
import Managers.Managers;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.BeforeEach;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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


}