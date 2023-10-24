import Managers.*;
import Tasks.*;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Тест таска", "Хотим протестить", Statuses.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Тест эпика", "тестим эпик");
        taskManager.createEpic(epic);
        Epic epic2 = new Epic("Тест эпика 2", "всё еще тестим эпик");
        taskManager.createEpic(epic2);
        Subtask subtask = new Subtask("Тест сабтаска", "тоже хотим потестить", Statuses.NEW, 2);
        Subtask subtask2 = new Subtask("Тест сабтаска2", "туц", Statuses.NEW, 3);
        Subtask subtask3 = new Subtask("Тест сабтаска3", "пуц", Statuses.NEW, 3);
        taskManager.createSubTask(subtask);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        Subtask subtask4 = new Subtask("test", "test", Statuses.DONE, 2, 4);
        taskManager.updateSubTask(subtask4);
        taskManager.deleteTaskById(4);
        System.out.println(taskManager.getEpicById(2));
        Task task2 = new Task("Обновляем", "Хотим обновить обычный таск", Statuses.DONE, 1);
        taskManager.updateTask(task2);
        System.out.println(taskManager.getAllEpicSubTasks(3));
        System.out.println(taskManager.getAllEpics());
        Subtask subtask5 = new Subtask("ssss", "Описываем тщательно и тестируем хорошо", Statuses.DONE, 3, 6);
        taskManager.updateSubTask(subtask5);
        System.out.println(taskManager.getAllEpics());
        Subtask subtask6 = taskManager.getSubTaskById(6);
        System.out.println("Конец теста");
        System.out.println(taskManager.getHistory());

    }
}
