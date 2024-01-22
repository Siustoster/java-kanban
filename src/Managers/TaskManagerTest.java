package Managers;

import Tasks.Epic;
import Tasks.Statuses;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;

    @BeforeEach
    abstract void createManager();

    @Test
    void checkSubTaskHasEpic() {
        final Epic epic = new Epic("Эпик", "Эпик1");
        final int epicId = taskManager.createEpic(epic);

        final Subtask subtask = new Subtask("Сабтаск", "Сабтаск1", Statuses.NEW, epicId);
        final int subtaskId = taskManager.createSubTask(subtask);

        final Epic newEpicFromSubtask = taskManager.getEpicById(taskManager.getSubTaskById(subtaskId).getEpicId());

        assertEquals(epic, newEpicFromSubtask);
    }

    @Test
    void createNewTask() {
        Task task = new Task("Задача", "Описание", Statuses.NEW);
        int taskId = taskManager.createTask(task);
        assertEquals(1, taskId);
    }

    @Test
    void createNewEpic() {
        Epic task = new Epic("Задача", "Описание");
        int taskId = taskManager.createEpic(task);
        assertEquals(1, taskId);
    }

    @Test
    void createNewSubtask() {
        Epic epic = new Epic("Эпик", "Эпик");
        int epicId = taskManager.createEpic(epic);
        Subtask task = new Subtask("Задача", "Описание", Statuses.NEW, epicId);
        int taskId = taskManager.createSubTask(task);
        assertEquals(2, taskId);
    }

    @Test
    void createNewSubtaskWithoudEpic() {

        Subtask task = new Subtask("Задача", "Описание", Statuses.NEW, 1);
        int taskId = taskManager.createSubTask(task);
        assertEquals(0, taskId);
    }

    @Test
    void getAllTasksGood() {
        Epic epic = new Epic("Эпик", "Эпик");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Задача", "Описание", Statuses.NEW, epicId);
        int taskId = taskManager.createSubTask(subtask);
        Task task = new Task("Таск", "фывфыв", Statuses.NEW);
        taskManager.createTask(task);
        List<Task> tasksList = taskManager.getAllTasks();
        assertEquals(1, tasksList.size());
    }

    @Test
    void getAllSubTasksGood() {
        Epic epic = new Epic("Эпик", "Эпик");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Задача", "Описание", Statuses.NEW, epicId);
        int taskId = taskManager.createSubTask(subtask);
        Task task = new Task("Таск", "фывфыв", Statuses.NEW);
        taskManager.createTask(task);
        List<Subtask> tasksList = taskManager.getAllSubTasks();
        assertEquals(1, tasksList.size());
    }

    @Test
    void getAllEpicTasksGood() {
        Epic epic = new Epic("Эпик", "Эпик");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Задача", "Описание", Statuses.NEW, epicId);
        int taskId = taskManager.createSubTask(subtask);
        Task task = new Task("Таск", "фывфыв", Statuses.NEW);
        taskManager.createTask(task);
        List<Epic> tasksList = taskManager.getAllEpics();
        assertEquals(1, tasksList.size());
    }

    @Test
    void deleteAllEpicsGood() {
        Epic epic = new Epic("Эпик", "Эпик");
        taskManager.createEpic(epic);
        List<Epic> tasksList = taskManager.getAllEpics();
        assertEquals(1, tasksList.size());
        taskManager.deleteAllEpics();
        tasksList = taskManager.getAllEpics();
        assertEquals(0, tasksList.size());
    }

    @Test
    void deleteAllTasksGood() {

        Task task = new Task("Таск", "фывфыв", Statuses.NEW);
        taskManager.createTask(task);
        List<Task> tasksList = taskManager.getAllTasks();
        assertEquals(1, tasksList.size());
        taskManager.getAllTasks();
        tasksList = taskManager.getAllTasks();
        assertEquals(1, tasksList.size());
    }

    @Test
    void deleteAllSubTasksGood() {
        Epic epic = new Epic("Эпик", "Эпик");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Задача", "Описание", Statuses.NEW, epicId);
        int taskId = taskManager.createSubTask(subtask);

        List<Subtask> tasksList = taskManager.getAllSubTasks();
        assertEquals(1, tasksList.size());
        taskManager.deleteAllSubTasks();
        tasksList = taskManager.getAllSubTasks();
        assertEquals(0, tasksList.size());
    }
    @Test
    void getTaskByIdValid() {
        Task task = new Task("ТАск","Таск",Statuses.NEW);
        int taskId = taskManager.createTask(task);
        Task taskWithId = new Task("ТАск","Таск",Statuses.NEW, taskId);

        Task taskFromManager = taskManager.getTaskById(taskId);
        int historyLength = taskManager.getHistory().size();
        Task taskFromHistory = taskManager.getHistory().get(historyLength-1);

        assertEquals(taskWithId,taskFromManager,"Таск из менеджера не сопадает");
        assertEquals(taskWithId,taskFromHistory,"Таск из истории не совпадает");
        assertEquals(1,historyLength);

    }
    @Test
    void getTaskByIdInvalid() {
        Task taskFromManager = taskManager.getTaskById(1);
        List<Task> historyList = taskManager.getHistory();

        assertNull(taskFromManager);
        assertEquals(0,historyList.size());
    }
    @Test
    void getSubTaskByIdValid() {
        Epic epic = new Epic("Эпик","Эпик");
        int epicId = taskManager.createEpic(epic);
        Subtask task = new Subtask("ТАск","Таск",Statuses.NEW,epicId);
        int taskId = taskManager.createSubTask(task);
        Subtask taskWithId = new Subtask("ТАск","Таск",Statuses.NEW,epicId,taskId);

        Task taskFromManager = taskManager.getSubTaskById(taskId);
        int historyLength = taskManager.getHistory().size();
        Task taskFromHistory = taskManager.getHistory().get(historyLength-1);

        assertEquals(taskWithId,taskFromManager,"Таск из менеджера не сопадает");
        assertEquals(taskWithId,taskFromHistory,"Таск из истории не совпадает");
        assertEquals(1,historyLength);

    }
    @Test
    void getSubTaskByIdInvalid() {
        Subtask taskFromManager = taskManager.getSubTaskById(1);
        List<Task> historyList = taskManager.getHistory();

        assertNull(taskFromManager);
        assertEquals(0,historyList.size());
    }
    @Test
    void getEpicTaskByIdValid() {
        Epic task = new Epic("ТАск","Таск");
        int taskId = taskManager.createEpic(task);
        Epic taskWithId = new Epic("ТАск","Таск", taskId);

        Task taskFromManager = taskManager.getEpicById(taskId);
        int historyLength = taskManager.getHistory().size();
        Task taskFromHistory = taskManager.getHistory().get(historyLength-1);

        assertEquals(taskWithId,taskFromManager,"Таск из менеджера не сопадает");
        assertEquals(taskWithId,taskFromHistory,"Таск из истории не совпадает");
        assertEquals(1,historyLength);
    }
    @Test
    void getEpicTaskByIdInvalid() {
        Epic taskFromManager = taskManager.getEpicById(1);
        List<Task> historyList = taskManager.getHistory();

        assertNull(taskFromManager);
        assertEquals(0,historyList.size());
    }
    @Test
    void updateTask() {
        Task task = new Task("Таск","Таск",Statuses.NEW);
        int taskId = taskManager.createTask(task);
        Task updatedTask = new Task("Хах", "хех",Statuses.IN_PROGRESS,taskId);
        taskManager.updateTask(updatedTask);
        Task taskFromManager = taskManager.getTaskById(taskId);
        assertEquals(updatedTask,taskFromManager);
    }
    @Test
    void updateEpic() {
        Epic task = new Epic("эпик'","Таск");
        int taskId = taskManager.createEpic(task);
        Epic updatedTask = new Epic("Хах", "хех",taskId);
        taskManager.updateEpic(updatedTask);
        Task taskFromManager = taskManager.getEpicById(taskId);
        assertEquals(updatedTask,taskFromManager);
    }
    @Test
    void updateSubtask() {
        Epic task = new Epic("эпик'","Таск");
        int epicId = taskManager.createEpic(task);
        Subtask subtask = new Subtask("sub","sub",Statuses.NEW,epicId);
        int subId = taskManager.createSubTask(subtask);
        Subtask updatedSubTask = new Subtask("subsub","sub",Statuses.IN_PROGRESS,epicId,subId);
        taskManager.updateSubTask(updatedSubTask);

        Task subtask2 = taskManager.getSubTaskById(subId);
        assertEquals(updatedSubTask,subtask2);
    }
}
