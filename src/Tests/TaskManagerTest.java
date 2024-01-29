package Tests;

import Managers.InvalidEpicTaskIdException;
import Managers.TaskManager;
import Managers.TimeCrossException;
import Tasks.Epic;
import Tasks.Statuses;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
        assertThrows(InvalidEpicTaskIdException.class, () -> taskManager.createSubTask(task));
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
        taskManager.deleteAllTasks();
        tasksList = taskManager.getAllTasks();
        assertEquals(0, tasksList.size());
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
        Task task = new Task("ТАск", "Таск", Statuses.NEW);
        int taskId = taskManager.createTask(task);
        Task taskWithId = new Task("ТАск", "Таск", Statuses.NEW, taskId);

        Task taskFromManager = taskManager.getTaskById(taskId);
        int historyLength = taskManager.getHistory().size();
        Task taskFromHistory = taskManager.getHistory().get(historyLength - 1);

        assertEquals(taskWithId, taskFromManager, "Таск из менеджера не сопадает");
        assertEquals(taskWithId, taskFromHistory, "Таск из истории не совпадает");
        assertEquals(1, historyLength);

    }

    @Test
    void getTaskByIdInvalid() {
        Task taskFromManager = taskManager.getTaskById(1);
        List<Task> historyList = taskManager.getHistory();

        assertNull(taskFromManager);
        assertEquals(0, historyList.size());
    }

    @Test
    void getSubTaskByIdValid() {
        Epic epic = new Epic("Эпик", "Эпик");
        int epicId = taskManager.createEpic(epic);
        Subtask task = new Subtask("ТАск", "Таск", Statuses.NEW, epicId);
        int taskId = taskManager.createSubTask(task);
        Subtask taskWithId = new Subtask("ТАск", "Таск", Statuses.NEW, epicId, taskId);

        Task taskFromManager = taskManager.getSubTaskById(taskId);
        int historyLength = taskManager.getHistory().size();
        Task taskFromHistory = taskManager.getHistory().get(historyLength - 1);

        assertEquals(taskWithId, taskFromManager, "Таск из менеджера не сопадает");
        assertEquals(taskWithId, taskFromHistory, "Таск из истории не совпадает");
        assertEquals(1, historyLength);

    }

    @Test
    void getSubTaskByIdInvalid() {
        Subtask taskFromManager = taskManager.getSubTaskById(1);
        List<Task> historyList = taskManager.getHistory();

        assertNull(taskFromManager);
        assertEquals(0, historyList.size());
    }

    @Test
    void getEpicTaskByIdValid() {
        Epic task = new Epic("ТАск", "Таск");
        int taskId = taskManager.createEpic(task);
        Epic taskWithId = new Epic("ТАск", "Таск", taskId);

        Task taskFromManager = taskManager.getEpicById(taskId);
        int historyLength = taskManager.getHistory().size();
        Task taskFromHistory = taskManager.getHistory().get(historyLength - 1);

        assertEquals(taskWithId, taskFromManager, "Таск из менеджера не сопадает");
        assertEquals(taskWithId, taskFromHistory, "Таск из истории не совпадает");
        assertEquals(1, historyLength);
    }

    @Test
    void getEpicTaskByIdInvalid() {
        Epic taskFromManager = taskManager.getEpicById(1);
        List<Task> historyList = taskManager.getHistory();

        assertNull(taskFromManager);
        assertEquals(0, historyList.size());
    }

    @Test
    void updateTask() {
        Task task = new Task("Таск", "Таск", Statuses.NEW);
        int taskId = taskManager.createTask(task);
        Task updatedTask = new Task("Хах", "хех", Statuses.IN_PROGRESS, taskId);
        taskManager.updateTask(updatedTask);
        Task taskFromManager = taskManager.getTaskById(taskId);
        assertEquals(updatedTask, taskFromManager);
    }

    @Test
    void updateEpic() {
        Epic task = new Epic("эпик'", "Таск");
        int taskId = taskManager.createEpic(task);
        Epic updatedTask = new Epic("Хах", "хех", taskId);
        taskManager.updateEpic(updatedTask);
        Task taskFromManager = taskManager.getEpicById(taskId);
        assertEquals(updatedTask, taskFromManager);
    }

    @Test
    void updateSubtask() {
        Epic task = new Epic("эпик'", "Таск");
        int epicId = taskManager.createEpic(task);
        Subtask subtask = new Subtask("sub", "sub", Statuses.NEW, epicId);
        int subId = taskManager.createSubTask(subtask);
        Subtask updatedSubTask = new Subtask("subsub", "sub", Statuses.IN_PROGRESS, epicId, subId);
        taskManager.updateSubTask(updatedSubTask);

        Task subtask2 = taskManager.getSubTaskById(subId);
        assertEquals(updatedSubTask, subtask2);
    }

    @Test
    void removeTaskById() {
        Task task = new Task("Task", "Task", Statuses.IN_PROGRESS);
        int taskId = taskManager.createTask(task);
        Task taskWithId = new Task("Task", "Task", Statuses.IN_PROGRESS, taskId);
        assertEquals(taskWithId, taskManager.getTaskById(taskId));
        taskManager.deleteTaskById(taskId);
        assertNull(taskManager.getTaskById(taskId));
    }

    @Test
    void getHistory() {
        Epic epic = new Epic("Эпик", "эпик");
        Task task = new Task("Таск", "таск", Statuses.IN_PROGRESS);
        int epicId = taskManager.createEpic(epic);
        int taskId = taskManager.createTask(task);
        Subtask subtask = new Subtask("Саб", "саб", Statuses.IN_PROGRESS, epicId);
        int subtaskId = taskManager.createSubTask(subtask);
        //Epic createdEpic = new Epic("Эпик", "эпик",epicId);
        //Subtask subtask2 = new Subtask("Саб","саб",Statuses.IN_PROGRESS,epicId)
        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epicId);
        taskManager.getSubTaskById(subtaskId);
        List<Task> History = taskManager.getHistory();

        assertEquals(List.of(subtask, epic, task), History);
    }

    @Test
    void addNewTask_withTimeCross_inEndTime() {
        LocalDateTime startTime = LocalDateTime.now();

        final Task task1 = new Task("name", "description", Statuses.NEW, startTime, 10);
        final Task task2 = new Task("name", "description", Statuses.NEW, startTime.plusMinutes(10), 10);

        taskManager.createTask(task1);
        final TimeCrossException exception = assertThrows(
                TimeCrossException.class,
                () -> taskManager.createTask(task2)
        );

        assertEquals("Задача пересекается во времени с задачей номер 1", exception.getMessage());
    }

    @Test
    void addNewTask_withTimeCross_inBetweenStartEndTime() {
        LocalDateTime startTime = LocalDateTime.now();

        final Task task1 = new Task("name", "description", Statuses.NEW, startTime, 10);
        final Task task2 = new Task("name", "description", Statuses.NEW, startTime.plusMinutes(9), 10);

        taskManager.createTask(task1);
        final TimeCrossException exception = assertThrows(
                TimeCrossException.class,
                () -> taskManager.createTask(task2)
        );

        assertEquals("Задача пересекается во времени с задачей номер 1", exception.getMessage());
    }

    @Test
    void addNewTask_withTimeCross_inBetweenStartEndTime_2() {
        LocalDateTime startTime = LocalDateTime.now();

        final Task task1 = new Task("name", "description", Statuses.NEW, startTime, 10);
        final Task task2 = new Task("name", "description", Statuses.NEW, startTime.minusMinutes(9), 10);

        taskManager.createTask(task1);
        final TimeCrossException exception = assertThrows(
                TimeCrossException.class,
                () -> taskManager.createTask(task2)
        );

        assertEquals("Задача пересекается во времени с задачей номер 1", exception.getMessage());
    }

    @Test
    void addNewSubTask_withTimeCross_inStartTime() {
        LocalDateTime startTime = LocalDateTime.now();

        final Epic epicTask = new Epic("epic_1", "description_1");
        final int epicTaskId = taskManager.createEpic(epicTask); // 1

        final Subtask subTask1 = new Subtask("sub_1", "sub_description_1", Statuses.NEW, epicTaskId, startTime, 10);
        final Subtask subTask2 = new Subtask("sub_2", "sub_description_2", Statuses.NEW, epicTaskId, startTime.minusMinutes(10), 10);
        final int subTaskId = taskManager.createSubTask(subTask1); // 2

        final TimeCrossException exception = assertThrows(
                TimeCrossException.class,
                () -> taskManager.createSubTask(subTask2)
        );

        assertEquals(2, subTaskId);
        assertEquals("Задача пересекается во времени с задачей номер 2", exception.getMessage());
    }

    @Test
    void addNewSubTask_withTimeCross_inEndTime() {
        LocalDateTime startTime = LocalDateTime.now();

        final Epic epicTask = new Epic("epic_1", "description_1");
        final int epicTaskId = taskManager.createEpic(epicTask); // 1

        final Subtask subTask1 = new Subtask("sub_1", "sub_description_1", Statuses.NEW, epicTaskId, startTime, 10);
        final Subtask subTask2 = new Subtask("sub_2", "sub_description_2", Statuses.NEW, epicTaskId, startTime.plusMinutes(10), 10);
        final int subTaskId = taskManager.createSubTask(subTask1); // 2

        final TimeCrossException exception = assertThrows(
                TimeCrossException.class,
                () -> taskManager.createSubTask(subTask2)
        );

        assertEquals(2, subTaskId);
        assertEquals("Задача пересекается во времени с задачей номер 2", exception.getMessage());
    }

    @Test
    void addNewSubTask_withTimeCross_inBetweenStartEndTime() {
        LocalDateTime startTime = LocalDateTime.now();

        final Epic epicTask = new Epic("epic_1", "description_1");
        final int epicTaskId = taskManager.createEpic(epicTask); // 1

        final Subtask subTask1 = new Subtask("sub_1", "sub_description_1", Statuses.NEW, epicTaskId, startTime, 10);
        final Subtask subTask2 = new Subtask("sub_2", "sub_description_2", Statuses.NEW, epicTaskId, startTime.plusMinutes(9), 10);
        final int subTaskId = taskManager.createSubTask(subTask1); // 2

        final TimeCrossException exception = assertThrows(
                TimeCrossException.class,
                () -> taskManager.createSubTask(subTask2)
        );

        assertEquals(2, subTaskId);
        assertEquals("Задача пересекается во времени с задачей номер 2", exception.getMessage());
    }

    @Test
    void addNewSubTask_withTimeCross_inBetweenStartEndTime_2() {
        LocalDateTime startTime = LocalDateTime.now();

        final Epic epicTask = new Epic("epic_1", "description_1");
        final int epicTaskId = taskManager.createEpic(epicTask); // 1

        final Subtask subTask1 = new Subtask("sub_1", "sub_description_1", Statuses.NEW, epicTaskId, startTime, 10);
        final Subtask subTask2 = new Subtask("sub_2", "sub_description_2", Statuses.NEW, epicTaskId, startTime.minusMinutes(9), 10);
        final int subTaskId = taskManager.createSubTask(subTask1); // 2

        final TimeCrossException exception = assertThrows(
                TimeCrossException.class,
                () -> taskManager.createSubTask(subTask2)
        );

        assertEquals(2, subTaskId);
        assertEquals("Задача пересекается во времени с задачей номер 2", exception.getMessage());
    }

    @Test
    void addNewSubTask_withoutTimeCross_beforeStartTime() {
        LocalDateTime startTime = LocalDateTime.now();

        final Epic epicTask = new Epic("epic_1", "description_1");
        final int epicTaskId = taskManager.createEpic(epicTask); // 1

        final Subtask subTask1 = new Subtask("sub_1", "sub_description_1", Statuses.NEW, epicTaskId, startTime, 10);
        final Subtask subTask2 = new Subtask("sub_2", "sub_description_2", Statuses.NEW, epicTaskId, startTime.minusMinutes(11), 10);
        final int subTaskId1 = taskManager.createSubTask(subTask1); // 2
        final int subTaskId2 = taskManager.createSubTask(subTask2); // 3

        assertEquals(2, subTaskId1);
        assertEquals(3, subTaskId2);
    }

    @Test
    void addNewSubTask_withoutTimeCross_afterEndTime() {
        LocalDateTime startTime = LocalDateTime.now();

        final Epic epicTask = new Epic("epic_1", "description_1");
        final int epicTaskId = taskManager.createEpic(epicTask); // 1

        final Subtask subTask1 = new Subtask("sub_1", "sub_description_1", Statuses.NEW, epicTaskId, startTime, 10);
        final Subtask subTask2 = new Subtask("sub_2", "sub_description_2", Statuses.NEW, epicTaskId, startTime.plusMinutes(11), 10);
        final int subTaskId1 = taskManager.createSubTask(subTask1); // 2
        final int subTaskId2 = taskManager.createSubTask(subTask2); // 3

        assertEquals(2, subTaskId1);
        assertEquals(3, subTaskId2);
    }

    @Test
    void getPrioretizedTaskList() {
        LocalDateTime startTime = LocalDateTime.now();
        final Task task1 = new Task("task1", "task must be third", Statuses.NEW, startTime.plusMinutes(10), 5);
        final Task task2 = new Task("task2", "task must be first", Statuses.NEW, startTime.minusMinutes(10), 5);
        final Task task3 = new Task("task3", "task must be second", Statuses.NEW, startTime, 5);
        int task1Id = taskManager.createTask(task1);
        int task2Id = taskManager.createTask(task2);
        int task3Id = taskManager.createTask(task3);
        final Task createdTask1 = new Task("task1", "task must be third", Statuses.NEW, task1Id, startTime.plusMinutes(10), 5);
        final Task createdTask2 = new Task("task2","task must be first",Statuses.NEW,task2Id,startTime.minusMinutes(10),5);
        final Task createdTask3 = new Task("task3","task must be second",Statuses.NEW,task3Id,startTime,5);
        List <Task> priorityList = taskManager.getPrioritizedTasks();
        assertEquals(priorityList.get(0),createdTask2);
        assertEquals(priorityList.get(1),createdTask3);
        assertEquals(priorityList.get(2),createdTask1);
    }
}
