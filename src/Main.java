import Managers.*;
import Tasks.*;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Тест таска", "Хотим протестить", Statuses.NEW);
        int taskId = taskManager.createTask(task);
        Epic epic = new Epic("Тест эпика", "тестим эпик");
        int epic1Id = taskManager.createEpic(epic);
        Epic epic2 = new Epic("Тест эпика 2", "всё еще тестим эпик");
        int epic2Id = taskManager.createEpic(epic2);
        Subtask subtask = new Subtask("Тест сабтаска", "тоже хотим потестить", Statuses.NEW, epic1Id);
        Subtask subtask2 = new Subtask("Тест сабтаска2", "туц", Statuses.NEW, epic2Id);
        Subtask subtask3 = new Subtask("Тест сабтаска3", "пуц", Statuses.NEW, epic2Id);
        int subtask1Id = taskManager.createSubTask(subtask);
        int subtask2Id = taskManager.createSubTask(subtask2);
        int subtask3Id = taskManager.createSubTask(subtask3);
        //Обновление//
        Task taskToUpdate = taskManager.getTaskById(taskId);
        taskToUpdate.setTaskStatus(Statuses.IN_PROGRESS);
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
        for (Task t : taskManager.getHistory()) {
            System.out.println(t);// посмотрите историю
        }
        System.out.println(taskManager.getHistory().size() <= 10); // проверите выполняется ли условие на размер истории

    }
}
