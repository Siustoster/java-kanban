package Tests;

import Managers.HttpTaskManager;
import Managers.Server.KVServer;
import Managers.Server.KVTaskClient;
import Tasks.Epic;
import Tasks.Statuses;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Managers.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    final String serverUrl = "http://localhost:8078";
    KVServer kvServer;

    @BeforeEach
    @Override
    void createManager() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskManager = Managers.getHttpTaskManager(serverUrl);
        } catch (IOException e) {
            System.out.println("Ошибка запуска KVServer");
        }
    }

    @AfterEach
    void tearDown() {
        kvServer.stop(0);
    }

    @Test
    void saveTasksToServer_emptyHistory() {
        String content;
        final int duration = 45;
        final LocalDateTime startTime = LocalDateTime.now();
        final LocalDateTime startSubTaskTime = startTime.plusMinutes(duration + 1);
        final String startTimeStr = startTime.format(taskManager.formatter);
        final String startSubTaskTimeStr = startSubTaskTime.format(taskManager.formatter);
        final String endTimeStr = startTime.plus(Duration.ofMinutes(duration)).format(taskManager.formatter);
        final String endSubTaskTimeStr = startSubTaskTime.plus(Duration.ofMinutes(duration)).format(taskManager.formatter);
        final Task task = new Task("name", "description", Statuses.NEW, startTime, duration);
        taskManager.createTask(task); // 1

        final Epic epicTask = new Epic("epic", "description");
        taskManager.createEpic(epicTask); // 2

        final Subtask subTask = new Subtask("sub", "sub_description", Statuses.NEW, 2, startSubTaskTime, duration);
        taskManager.createSubTask(subTask); // 3

        KVTaskClient kvTaskClient = new KVTaskClient(serverUrl);
        content = kvTaskClient.load("TaskManager_01");

        assertEquals(
                "id,type,name,status,description,epic,startTime,endTime,duration\r\n" +
                        "1,Task,name,NEW,description,," + startTimeStr + "," + endTimeStr + "," + duration + "\r\n" +
                        "2,Epic,epic,NEW,description,," + startSubTaskTimeStr + "," + endSubTaskTimeStr + "," + duration + "\r\n" +
                        "3,Subtask,sub,NEW,sub_description,2," + startSubTaskTimeStr + "," + endSubTaskTimeStr + "," + duration + "\r\n\r\n",
                content
        );
    }

    @Test
    void saveTasksToServer_withHistory() {
        String content;
        final int duration = 45;
        final LocalDateTime startTime = LocalDateTime.now();
        final LocalDateTime startSubTaskTime = startTime.plusMinutes(duration + 1);
        final String startTimeStr = startTime.format(taskManager.formatter);
        final String startSubTaskTimeStr = startSubTaskTime.format(taskManager.formatter);
        final String endTimeStr = startTime.plus(Duration.ofMinutes(duration)).format(taskManager.formatter);
        final String endSubTaskTimeStr = startSubTaskTime.plus(Duration.ofMinutes(duration)).format(taskManager.formatter);
        final Task task = new Task("name", "description", Statuses.NEW, startTime, duration);
        final int taskId = taskManager.createTask(task); // 1

        final Epic epicTask = new Epic("epic", "description");
        final int epicTaskId = taskManager.createEpic(epicTask); // 2

        final Subtask subTask = new Subtask("sub", "sub_description", Statuses.NEW, 2, startSubTaskTime, duration);
        final int subTaskId = taskManager.createSubTask(subTask); // 3

        taskManager.getSubTaskById(subTaskId);
        taskManager.getTaskById(taskId);
        taskManager.getEpicById(epicTaskId);

        KVTaskClient kvTaskClient = new KVTaskClient(serverUrl);
        content = kvTaskClient.load("TaskManager_01");

        assertEquals(
                "id,type,name,status,description,epic,startTime,endTime,duration\r\n" +
                        "1,Task,name,NEW,description,," + startTimeStr + "," + endTimeStr + "," + duration + "\r\n" +
                        "2,Epic,epic,NEW,description,," + startSubTaskTimeStr + "," + endSubTaskTimeStr + "," + duration + "\r\n" +
                        "3,Subtask,sub,NEW,sub_description,2," + startSubTaskTimeStr + "," + endSubTaskTimeStr + "," + duration + "\r\n\r\n" +
                        "2,1,3",
                content
        );
    }


}