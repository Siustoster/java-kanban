import Tasks.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task = new Task("Тест таска", "Хотим протестить", "NEW");
        taskManager.createTask(task);
        Epic epic = new Epic("Тест эпика", "тестим эпик");
        taskManager.createEpic(epic);
        Epic epic2 = new Epic("Тест эпика 2", "всё еще тестим эпик");
        taskManager.createEpic(epic2);
        Subtask subtask = new Subtask("Тест сабтаска", "тоже хотим потестить", "NEW", 2);
        Subtask subtask2 = new Subtask("Тест сабтаска2", "туц", "NEW", 3);
        Subtask subtask3 = new Subtask("Тест сабтаска3", "пуц", "NEW", 3);
        taskManager.createSubTask(subtask);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubTasks());
        Subtask subtask4 = new Subtask("test", "test", "DONE", 2, 4);
        taskManager.updateSubTask(subtask4);
        taskManager.deleteTaskById(4);
        System.out.println(taskManager.getEpicById(2));
        Task task2 = new Task("Обновляем", "Хотим обновить обычный таск", "DONE", 1);
        taskManager.updateTask(task2);
        System.out.println(taskManager.getAllEpicSubTasks(3));
        System.out.println(taskManager.getAllEpics());
        Subtask subtask5 = new Subtask("ssss","Описываем тщательно и тестируем хорошо","DONE",3,6);
        taskManager.updateSubTask(subtask5);
        System.out.println(taskManager.getAllEpics());
        System.out.println("Конец теста");

    }
}
