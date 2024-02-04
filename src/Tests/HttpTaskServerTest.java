package Tests;
import Managers.Server.HttpTaskServer;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import Managers.Managers;
import Managers.TaskManager;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Tasks.Subtask;
import Tasks.Task;
import Tasks.Epic;
import Tasks.Statuses;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Task task;
    Epic epicTask;
    Subtask subTask;

    Gson gson =  new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }

            return LocalDateTime.parse(jsonReader.nextString(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        }
    }

    @BeforeEach
    void setUp() throws IOException {
       // Gson gson = Managers.getGson();
        taskManager = Managers.getDefault();
        LocalDateTime localDateTime = LocalDateTime.parse("01.01.2024 08:00",
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        task = new Task( "task name", "task description",Statuses.NEW, localDateTime, 10);
        taskManager.createTask(task);
        epicTask = new Epic("epic name", "epic description");
        taskManager.createEpic(epicTask);
        subTask = new Subtask( "subtask name", "subtask description",Statuses.NEW, 2, localDateTime.plusMinutes(20), 10);
        taskManager.createSubTask(subTask);

        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.startServer();
    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stopServer(0);
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type actualType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), actualType);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(task, actual.get(0));
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type actualType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> actual = gson.fromJson(response.body(), actualType);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(epicTask, actual.get(0));
    }

    @Test
    void getAllSubTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type actualType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), actualType);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(subTask, actual.get(0));
    }

    @Test
    void getTaskById_validId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task actual = gson.fromJson(response.body(), Task.class);
        assertEquals(task, actual);
    }

    @Test
    void getTaskById_invalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task?id=100");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Задача с id = 100 не найдена", response.body());
    }

    @Test
    void getEpicTaskById_validId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask?id=2");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic actual = gson.fromJson(response.body(), Epic.class);
        assertEquals(epicTask, actual);
    }

    @Test
    void getEpicTaskById_invalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask?id=100");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Эпик задача с id = 100 не найдена", response.body());
    }

    @Test
    void getSubTaskById_validId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask actual = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subTask, actual);
    }

    @Test
    void getSubTaskById_invalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=100");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Подзадача с id = 100 не найдена", response.body());
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        taskManager.getSubTaskById(3);
        taskManager.getEpicById(2);
        taskManager.getTaskById(1);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type actualType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), actualType);

        assertNotNull(actual);
        assertEquals(3, actual.size());
        assertEquals("[\n" +
                "  {\n" +
                "    \"taskName\": \"task name\",\n" +
                "    \"taskDescription\": \"task description\",\n" +
                "    \"taskId\": 1,\n" +
                "    \"taskStatus\": \"NEW\",\n" +
                "    \"startTime\": \"01.01.2024 08:00\",\n" +
                "    \"duration\": 10\n" +
                "  },\n" +
                "  {\n" +
                "    \"subTasksList\": [\n" +
                "      3\n" +
                "    ],\n" +
                "    \"endTime\": \"01.01.2024 08:30\",\n" +
                "    \"taskName\": \"epic name\",\n" +
                "    \"taskDescription\": \"epic description\",\n" +
                "    \"taskId\": 2,\n" +
                "    \"taskStatus\": \"NEW\",\n" +
                "    \"startTime\": \"01.01.2024 08:20\",\n" +
                "    \"duration\": 10\n" +
                "  },\n" +
                "  {\n" +
                "    \"epicId\": 2,\n" +
                "    \"taskName\": \"subtask name\",\n" +
                "    \"taskDescription\": \"subtask description\",\n" +
                "    \"taskId\": 3,\n" +
                "    \"taskStatus\": \"NEW\",\n" +
                "    \"startTime\": \"01.01.2024 08:20\",\n" +
                "    \"duration\": 10\n" +
                "  }\n" +
                "]", response.body());
    }

    @Test
    void getHistory_voidHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type actualType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), actualType);

        assertNotNull(actual);
        assertEquals(0, actual.size());
        assertEquals("[]", response.body());
    }

    @Test
    void getPrioritized() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/prioritized");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type actualType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), actualType);

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals("[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"name\": \"task name\",\n" +
                "    \"description\": \"task description\",\n" +
                "    \"startTime\": \"01.01.2024 08:00\",\n" +
                "    \"duration\": 10\n" +
                "  },\n" +
                "  {\n" +
                "    \"epicTaskId\": 2,\n" +
                "    \"id\": 3,\n" +
                "    \"status\": \"NEW\",\n" +
                "    \"name\": \"subtask name\",\n" +
                "    \"description\": \"subtask description\",\n" +
                "    \"startTime\": \"01.01.2024 08:20\",\n" +
                "    \"duration\": 10\n" +
                "  }\n" +
                "]", response.body());
    }

    @Test
    void deleteTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        List<Task> tasksBeforeDelete = taskManager.getAllTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksAfterDelete = taskManager.getAllTasks();

        assertEquals(200, response.statusCode());
        assertEquals("Все таски удалены", response.body());
        assertEquals(1, tasksBeforeDelete.size());
        assertEquals(0, tasksAfterDelete.size());
    }

    @Test
    void deleteSubTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        List<Subtask> subTasksBeforeDelete = taskManager.getAllSubTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subTasksAfterDelete = taskManager.getAllSubTasks();

        assertEquals(200, response.statusCode());
        assertEquals("Все сабтаски удалены", response.body());
        assertEquals(1, subTasksBeforeDelete.size());
        assertEquals(0, subTasksAfterDelete.size());
    }

    @Test
    void deleteEpicTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        List<Epic> epicTasksBeforeDelete = taskManager.getAllEpics();
        List<Subtask> subTasksBeforeDelete = taskManager.getAllSubTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> subTasksAfterDelete = taskManager.getAllEpics();
        List<Subtask> epicTasksAfterDelete = taskManager.getAllSubTasks();

        assertEquals(200, response.statusCode());
        assertEquals("Все эпики удалены", response.body());
        assertEquals(1, epicTasksBeforeDelete.size());
        assertEquals(1, subTasksBeforeDelete.size());
        assertEquals(0, epicTasksAfterDelete.size());
        assertEquals(0, subTasksAfterDelete.size());
    }

    @Test
    void deleteTaskById_validId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        List<Task> tasksBeforeDelete = taskManager.getAllTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksAfterDelete = taskManager.getAllTasks();

        assertEquals(200, response.statusCode());
        assertEquals("Задача с Шd = 1 удалена.", response.body());
        assertEquals(1, tasksBeforeDelete.size());
        assertEquals(0, tasksAfterDelete.size());
    }

    @Test
    void deleteTaskById_invalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task?id=100");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача с Id = 100 не найдена.", response.body());
    }

    @Test
    void deleteSubTaskById_validId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        List<Subtask> subTasksBeforeDelete = taskManager.getAllSubTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subTasksAfterDelete = taskManager.getAllSubTasks();

        assertEquals(200, response.statusCode());
        assertEquals("Задача с Id = 3 удалена.", response.body());
        assertEquals(1, subTasksBeforeDelete.size());
        assertEquals(0, subTasksAfterDelete.size());
    }

    @Test
    void deleteSubTaskById_invalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=100");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача с Id = 100 не найдена.", response.body());
    }

    @Test
    void deleteEpicTaskById_validId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask?id=2");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        List<Epic> epicTasksBeforeDelete = taskManager.getAllEpics();
        List<Subtask> subTasksBeforeDelete = taskManager.getAllSubTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> subTasksAfterDelete = taskManager.getAllEpics();
        List<Subtask> epicTasksAfterDelete = taskManager.getAllSubTasks();

        assertEquals(200, response.statusCode());
        assertEquals("Задача с Id = 2 удалена.", response.body());
        assertEquals(1, epicTasksBeforeDelete.size());
        assertEquals(1, subTasksBeforeDelete.size());
        assertEquals(0, epicTasksAfterDelete.size());
        assertEquals(0, subTasksAfterDelete.size());
    }

    @Test
    void deleteEpicTaskById_invalidId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask?id=100");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача с Id = 100 не найдена", response.body());
    }

    @Test
    void postTask() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"название\",\n" +
                "\"taskDescription\": \"описание\",\n" +
                "\"startTime\": \"07.01.2024 08:45\",\n" +
                "\"duration\": \"60\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        List<Task> tasksBeforeAdd = taskManager.getAllTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksAfterAdd = taskManager.getAllTasks();
        Task actual = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(1, tasksBeforeAdd.size());
        assertEquals(2, tasksAfterAdd.size());
        assertEquals(tasksAfterAdd.get(1), actual);
    }

    @Test
    void postTask_withCrossOnTimeLine() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"название\",\n" +
                "\"taskDescription\": \"описание\",\n" +
                "\"startTime\": \"01.01.2024 08:05\",\n" +
                "\"duration\": \"60\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Задача пересекается во времени с задачей номер 1", response.body());
    }

    @Test
    void postTask_forgotAddName() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskDescription\": \"описание\",\n" +
                "\"startTime\": \"07.01.2024 08:45\",\n" +
                "\"duration\": \"60\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр name", response.body());
    }

    @Test
    void postTask_forgotAddDescription() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"название\",\n" +
                "\"startTime\": \"07.01.2024 08:45\",\n" +
                "\"duration\": \"60\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр description", response.body());
    }

    @Test
    void postTask_AddDurationButForgotAddStartTime() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"название\",\n" +
                "\"taskDescription\": \"описание\",\n" +
                "\"duration\": \"60\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Была указанна продолжительности (duration), но отсутствует параметр startTime", response.body());
    }

    @Test
    void postSubTask() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2\",\n" +
                "\"taskDescription\": \"Описание подзадачи 2\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2,\n" +
                "\"startTime\": \"02.01.2024 11:00\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        List<Subtask> subTasksBeforeAdd = taskManager.getAllSubTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subTasksAfterAdd = taskManager.getAllSubTasks();
        Task actual = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(1, subTasksBeforeAdd.size());
        assertEquals(2, subTasksAfterAdd.size());
        assertEquals(subTasksAfterAdd.get(1), actual);
    }

    @Test
    void postSubTask_withCrossOnTimeLine() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2\",\n" +
                "\"taskDescription\": \"Описание подзадачи 2\",\n" +
                "\"duration\": 60,\n" +
                "\"startTime\": \"01.01.2024 08:05\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Задача пересекается во времени с задачей номер 1", response.body());
    }

    @Test
    void postSubTask_forgotAddEpicId() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2\",\n" +
                "\"taskDescription\": \"Описание подзадачи 2\",\n" +
                "\"duration\": 60,\n" +
                "\"startTime\": \"02.01.2024 11:00\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр epicTaskId или он равен 0", response.body());
    }

    @Test
    void postSubTask_forgotAddName() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskDescription\": \"Описание подзадачи 2\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2,\n" +
                "\"startTime\": \"02.01.2024 11:00\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр name", response.body());
    }

    @Test
    void postSubTask_forgotAddDescription() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2,\n" +
                "\"startTime\": \"02.01.2024 11:00\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр description", response.body());
    }

    @Test
    void postSubTask_AddDurationButForgotAddStartTime() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2\",\n" +
                "\"taskDescription\": \"Описание подзадачи 2\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Была указанна продолжительности (duration), но отсутствует параметр startTime", response.body());
    }

    @Test
    void postEpicTask() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"Эпик 2\",\n" +
                "\"taskDescription\": \"Описание эпика 2\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        List<Epic> epicTasksBeforeAdd = taskManager.getAllEpics();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicTasksAfterAdd = taskManager.getAllEpics();
        Task actual = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(1, epicTasksBeforeAdd.size());
        assertEquals(2, epicTasksAfterAdd.size());
        assertEquals(epicTasksAfterAdd.get(1), actual);
    }

    @Test
    void postEpicTask_forgotAddName() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskDescription\": \"Описание эпика 2\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр name", response.body());
    }

    @Test
    void postEpicTask_forgotAddDescription() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"Эпик 2\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр description", response.body());
    }

    @Test
    void putTask() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"название - редактировано!\",\n" +
                "\"taskDescription\": \"описание - редактировано!\",\n" +
                "\"startTime\": \"07.01.2024 08:45\",\n" +
                "\"duration\": 60,\n" +
                "\"taskId\": 1,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        List<Task> tasksBeforeUpdate = taskManager.getAllTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksAfterUpdate = taskManager.getAllTasks();
        Task actual = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(1, tasksBeforeUpdate.size());
        assertEquals(1, tasksAfterUpdate.size());
        assertEquals(tasksAfterUpdate.get(0), actual);
    }

    @Test
    void putTask_invalidId() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"название - редактировано!\",\n" +
                "\"taskDescription\": \"описание - редактировано!\",\n" +
                "\"startTime\": \"07.01.2024 08:45\",\n" +
                "\"duration\": 60,\n" +
                "\"taskId\": 100,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача с id = 100 не найдена", response.body());
    }

    @Test
    void putTask_forgotAddId() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"название - редактировано!\",\n" +
                "\"taskDescription\": \"описание - редактировано!\",\n" +
                "\"startTime\": \"07.01.2024 08:45\",\n" +
                "\"duration\": 60,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр id или он равен 0", response.body());
    }

    @Test
    void putTask_forgotAddName() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskDescription\": \"описание - редактировано!\",\n" +
                "\"startTime\": \"07.01.2024 08:45\",\n" +
                "\"duration\": 60,\n" +
                "\"taskId\": 1,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр name", response.body());
    }

    @Test
    void putTask_forgotAddDescription() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"название - редактировано!\",\n" +
                "\"startTime\": \"07.01.2024 08:45\",\n" +
                "\"duration\": 60,\n" +
                "\"taskId\": 1,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр description", response.body());
    }

    @Test
    void putTask_AddDurationButForgotAddStartTime() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"название - редактировано!\",\n" +
                "\"taskDescription\": \"описание - редактировано!\",\n" +
                "\"duration\": 60,\n" +
                "\"taskId\": 1,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Была указанна продолжительности (duration), но отсутствует параметр startTime", response.body());
    }

    @Test
    void putSubTask() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2 - редактировано!\",\n" +
                "\"taskDescription\": \"Описание подзадачи 2 - редактировано!\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2,\n" +
                "\"startTime\": \"02.01.2024 11:00\",\n" +
                "\"taskId\": 3,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        List<Subtask> subTasksBeforeUpdate = taskManager.getAllSubTasks();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subTasksAfterUpdate = taskManager.getAllSubTasks();
        Task actual = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(1, subTasksBeforeUpdate.size());
        assertEquals(1, subTasksAfterUpdate.size());
        assertEquals(subTasksAfterUpdate.get(0), actual);
    }

    @Test
    void putSubTask_invalidId() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2 - редактировано!\",\n" +
                "\"taskDescription\": \"Описание подзадачи 2 - редактировано!\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2,\n" +
                "\"startTime\": \"02.01.2024 11:00\",\n" +
                "\"taskId\": 100,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Подзадача с id = 100 не найдена", response.body());
    }

    @Test
    void putSubTask_forgotAddId() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2 - редактировано!\",\n" +
                "\"taskDescription\": \"Описание подзадачи 2 - редактировано!\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2,\n" +
                "\"startTime\": \"02.01.2024 11:00\",\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр id или он равен 0", response.body());
    }

    @Test
    void putSubTask_forgotAddName() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskDescription\": \"Описание подзадачи 2 - редактировано!\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2,\n" +
                "\"startTime\": \"02.01.2024 11:00\",\n" +
                "\"taskId\": 3,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр name", response.body());
    }

    @Test
    void putSubTask_forgotAddDescription() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2 - редактировано!\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2,\n" +
                "\"startTime\": \"02.01.2024 11:00\",\n" +
                "\"taskId\": 3,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр description", response.body());
    }

    @Test
    void putSubTask_AddDurationButForgotAddStartTime() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"подзадача 2 - редактировано!\",\n" +
                "\"taskDescription\": \"Описание подзадачи 2 - редактировано!\",\n" +
                "\"duration\": 60,\n" +
                "\"epicId\": 2,\n" +
                "\"taskId\": 3,\n" +
                "\"taskStatus\": \"IN_PROGRESS\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Была указанна продолжительности (duration), но отсутствует параметр startTime", response.body());
    }

    @Test
    void putEpicTask() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskId\": 2,\n" +
                "\"taskName\": \"Эпик 2 - редактировано!\",\n" +
                "\"taskDescription\": \"Описание эпика 2 - редактировано!\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        List<Epic> epicTasksBeforeUpdate = taskManager.getAllEpics();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicTasksAfterUpdate = taskManager.getAllEpics();
        Task actual = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(1, epicTasksBeforeUpdate.size());
        assertEquals(1, epicTasksAfterUpdate.size());
        assertEquals(epicTasksAfterUpdate.get(0), actual);
    }

    @Test
    void putEpicTask_invalidId() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskId\": 100,\n" +
                "\"taskName\": \"Эпик 2 - редактировано!\",\n" +
                "\"taskDescription\": \"Описание эпика 2 - редактировано!\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Эпик задача с id = 100 не найдена", response.body());
    }

    @Test
    void putEpicTask_forgotAddId() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskName\": \"Эпик 2 - редактировано!\",\n" +
                "\"taskDescription\": \"Описание эпика 2 - редактировано!\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр id или он равен 0", response.body());
    }

    @Test
    void putEpicTask_forgotAddName() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskId\": 2,\n" +
                "\"taskDescription\": \"Описание эпика 2 - редактировано!\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр name", response.body());
    }

    @Test
    void putEpicTask_forgotAddDescription() throws IOException, InterruptedException {
        String json = "{\n" +
                "\"taskId\": 2,\n" +
                "\"taskName\": \"Эпик 2 - редактировано!\"\n" +
                "}";
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epictask");
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json;charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Отсутствует обязательный параметр description", response.body());
    }

    {
    }
}
