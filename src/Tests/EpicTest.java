package Tests;

import Managers.InMemoryTaskManager;
import Managers.TaskManager;
import Tasks.Epic;
import Tasks.Statuses;
import Tasks.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager;
    @BeforeEach
    public void createTaskManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void statusOfCreatedEpicTaskShouldBe_NEW() {
        final Epic epicTask = new Epic("Эпик", "Описание");
        final Statuses status = epicTask.getTaskStatus();
        assertEquals(Statuses.NEW, status);
    }

    @Test
    void statusOfEpicTaskWithNewSubtaskShouldBe_NEW() {
        final Epic epicTask = new Epic("Эпик", "Описание");
        final int epicTaskId = taskManager.createEpic(epicTask);

        final Subtask subTask1 = new Subtask( "Сабтаск", "Сабтаск1",Statuses.NEW, epicTaskId);
        taskManager.createSubTask(subTask1);

        final Subtask subTask2 = new Subtask( "Сабтаск 2", "Сабтаск2",Statuses.NEW, epicTaskId);
        taskManager.createTask(subTask2);

        final Statuses status = epicTask.getTaskStatus();
        assertEquals(Statuses.NEW, status);
    }
    @Test
    void statusOfEpicTaskWithDoneSubtaskShouldBe_DONE() {
        final Epic epicTask = new Epic("Эпик", "Описание");
        final int epicTaskId = taskManager.createEpic(epicTask);

        final Subtask subTask1 = new Subtask( "Сабтаск", "Сабтаск1",Statuses.DONE, epicTaskId);
        taskManager.createSubTask(subTask1);

        final Subtask subTask2 = new Subtask( "Сабтаск 2", "Сабтаск2",Statuses.DONE, epicTaskId);
        taskManager.createTask(subTask2);

        final Statuses status = epicTask.getTaskStatus();
        assertEquals(Statuses.DONE, status);
    }
    @Test
    void statusOfEpicTaskWithDifferentStatusesSubtaskShouldBe_IN_PROGRESS() {
        final Epic epicTask = new Epic("Эпик", "блах");
        final int epicTaskId = taskManager.createEpic(epicTask);

        final Subtask subTask1 = new Subtask( "Сабтаск", "сабтаск 1",Statuses.NEW, epicTaskId);
        taskManager.createSubTask(subTask1);

        final Subtask subTask2 = new Subtask( "Сабтаск2", "сабтассс",Statuses.DONE, epicTaskId);
        taskManager.createSubTask(subTask2);

        final Statuses status = epicTask.getTaskStatus();
        assertEquals(Statuses.IN_PROGRESS, status);
    }



    @Test
    void statusOfEpicTaskWithInProgressSubtaskShouldBe_IN_PROGRESS() {
        final Epic epicTask = new Epic("Эпик", "Фыыв");
        final int epicTaskId = taskManager.createEpic(epicTask);

        final Subtask subTask1 = new Subtask( "Сабстаск", "123123",Statuses.IN_PROGRESS, epicTaskId);
        taskManager.createSubTask(subTask1);

        final Subtask subTask2 = new Subtask("Сабтаск2", "1231231231",Statuses.IN_PROGRESS, epicTaskId);
        taskManager.createSubTask(subTask2);

        final Statuses status = epicTask.getTaskStatus();
        assertEquals(Statuses.IN_PROGRESS, status);
    }

}