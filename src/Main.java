import Managers.*;
import Tasks.*;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class Main {

    public static void main(String[] args) throws IOException {
        //TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Тест таска", "Хотим протестить", Statuses.NEW);
        //int taskId = taskManager.createTask(task);
        Epic epic = new Epic("Тест эпика", "тестим эпик");
        /*int epic1Id = taskManager.createEpic(epic);
        Epic epic2 = new Epic("Тест эпика 2", "всё еще тестим эпик");
        int epic2Id = taskManager.createEpic(epic2);
        Subtask subtask = new Subtask("Тест сабтаска", "тоже хотим потестить", Statuses.NEW, epic1Id);
        Subtask subtask2 = new Subtask("Тест сабтаска2", "туц", Statuses.NEW, epic1Id);
        Subtask subtask3 = new Subtask("Тест сабтаска3", "пуц", Statuses.NEW, epic1Id);
        int subtask1Id = taskManager.createSubTask(subtask);
        int subtask2Id = taskManager.createSubTask(subtask2);
        int subtask3Id = taskManager.createSubTask(subtask3);
        //Обновление//
        //Task taskToUpdate = taskManager.getTaskById(taskId);
        //   taskToUpdate.setTaskStatus(Statuses.IN_PROGRESS);
        taskManager.updateTask(task); // аналогично для Subtask и Epic
        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epic1Id);
        taskManager.getEpicById(epic2Id);
        taskManager.getSubTaskById(subtask1Id);
        taskManager.getSubTaskById(subtask2Id);
        taskManager.getSubTaskById(subtask3Id);
        taskManager.getEpicById(epic1Id);
        taskManager.getEpicById(epic1Id);
        taskManager.getEpicById(epic1Id);
        taskManager.getEpicById(epic1Id);
        taskManager.getEpicById(epic1Id);
        taskManager.getEpicById(epic1Id);
       // taskManager.deleteTaskById(epic1Id);
        for (Task t : taskManager.getHistory()) {
            System.out.println(t);// посмотрите историю
        }
        */
        File file = new File("D:\\Repos\\java-kanban2", "ManagerState.csv");
        //ПРИЛОЖИЛ В ГИТЕ ФАЙЛ (ЗАПОЛНЕННЫЙ) С КОТОРЫМ НАЧИНАЛ ТЕСТИРОВАНИЕ
        if (!file.exists())
            Files.createFile(file.toPath());
        FileBackedTasksManager writeTasksManager = Managers.getDefault(file);

        //FileBackedTasksManager writeTasksManager  = FileBackedTasksManager.loadFromFile(file);
        int epic1Id = writeTasksManager.createEpic(epic);
        Subtask subtask = new Subtask("Тест сабтаска", "тоже хотим потестить", Statuses.NEW, epic1Id);
        Subtask subtask2 = new Subtask("Тест сабтаска2", "туц", Statuses.NEW, epic1Id);
        Subtask subtask3 = new Subtask("Тест сабтаска3", "пуц", Statuses.NEW, epic1Id);
        int subtask1Id = writeTasksManager.createSubTask(subtask);
        int subtask2Id = writeTasksManager.createSubTask(subtask2);
        int subtask3Id = writeTasksManager.createSubTask(subtask3);
        int taskId = writeTasksManager.createTask(task);
        writeTasksManager.getEpicById(epic1Id);
        writeTasksManager.getSubTaskById(subtask3Id);
        writeTasksManager.getSubTaskById(subtask2Id);
        writeTasksManager.getSubTaskById(subtask1Id);
        File file2 = new File("D:\\Repos\\java-kanban2", "ManagerState.csv"); //Здесь вручную меняю
        //наполнение файла пока пауза в дебагере, проверяю сравнение менеджеров

        FileBackedTasksManager readTasksManager = FileBackedTasksManager.loadFromFile(file2);
        System.out.println("менеджеры совпадают: " + readTasksManager.equals(writeTasksManager));
    }
}
