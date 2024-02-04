package Managers.Server;

import Exceptions.InvalidTaskIdException;
import Exceptions.TimeCrossException;
import Managers.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import Managers.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        Gson gson = Managers.getGson();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager, gson));
    }

    public void startServer() {
        httpServer.start();
        System.out.println("Сервер запущен на " + PORT + " порту");
    }

    public void stopServer(int delay) {
        httpServer.stop(delay);
        System.out.println("Сервер остановлен");
    }

    public class TaskHandler implements HttpHandler {
        private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
        private final TaskManager manager;
        private final Gson gson;

        public TaskHandler(TaskManager manager, Gson gson) {
            this.manager = manager;
            this.gson = gson;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Optional<Map<String, String>> paramsOptional = getParams(exchange.getRequestURI().getQuery());
            Endpoints endpoint = getEndpoint(method, path, paramsOptional.isPresent());
            Map<String, String> params = paramsOptional.orElse(null);
            int id = 0;
            if (endpoint.equals(Endpoints.GET_TASK_BY_ID) || endpoint.equals(Endpoints.GET_SUBTASK_BY_ID)
                    || endpoint.equals(Endpoints.GET_EPICTASK_BY_ID) || endpoint.equals(Endpoints.DELETE_TASK_BY_ID)
                    || endpoint.equals(Endpoints.DELETE_SUBTASK_BY_ID)
                    || endpoint.equals(Endpoints.DELETE_EPICTASK_BY_ID)) {
                if (params == null || params.get("id") == null) {
                    writeResponse(exchange, "Необходимо передать Id задачи", 400);
                    return;
                }
                try {
                    id = Integer.parseInt(params.get("id"));
                } catch (NumberFormatException | NullPointerException e) {
                    writeResponse(exchange, "Неверный формат Id задачи", 400);
                }
            }

            switch (endpoint) {
                case GET_TASKS:
                    handleGetTasks(exchange);
                    break;
                case GET_SUBTASKS:
                    handleGetSubTasks(exchange);
                    break;
                case GET_EPICTASKS:
                    handleGetEpicTasks(exchange);
                    break;
                case GET_TASK_BY_ID:
                    handleGetTaskById(exchange, id);
                    break;
                case GET_SUBTASK_BY_ID:
                    handleGetSubTaskById(exchange, id);
                    break;
                case GET_EPICTASK_BY_ID:
                    handleGetEpicTaskById(exchange, id);
                    break;
                case GET_HISTORY:
                    handleGetHistory(exchange);
                    break;
                case GET_PRIORITIZED:
                    handleGetPrioritized(exchange);
                    break;
                case POST_TASK:
                    handlePostTask(exchange);
                    break;
                case POST_SUBTASK:
                    handlePostSubTask(exchange);
                    break;
                case POST_EPICTASK:
                    handlePostEpicTask(exchange);
                    break;
                case PUT_TASK:
                    handlePutTask(exchange);
                    break;
                case PUT_SUBTASK:
                    handlePutSubTask(exchange);
                    break;
                case PUT_EPICTASK:
                    handlePutEpicTask(exchange);
                    break;
                case DELETE_TASKS:
                    handleDeleteTasks(exchange);
                    break;
                case DELETE_SUBTASKS:
                    handleDeleteSubTasks(exchange);
                    break;
                case DELETE_EPICTASKS:
                    handleDeleteEpicTasks(exchange);
                case DELETE_TASK_BY_ID:
                case DELETE_SUBTASK_BY_ID:
                case DELETE_EPICTASK_BY_ID:
                    handleDeleteTaskById(exchange, id);
                    break;
                case UNKNOWN:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }

        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            List<Task> taskList = manager.getAllTasks();
            writeResponse(exchange, gson.toJson(taskList), 200);
        }

        private void handleGetSubTasks(HttpExchange exchange) throws IOException {
            List<Subtask> taskList = manager.getAllSubTasks();
            writeResponse(exchange, gson.toJson(taskList), 200);
        }

        private void handleGetEpicTasks(HttpExchange exchange) throws IOException {
            List<Epic> taskList = manager.getAllEpics();
            writeResponse(exchange, gson.toJson(taskList), 200);
        }

        private void handleGetTaskById(HttpExchange exchange, int Id) throws IOException {
            Task task;
            try {
                task = manager.getTaskById(Id);
                if (!(task==null))
                    writeResponse(exchange, gson.toJson(task), 200);
                else
                    writeResponse(exchange, "Задача с Id = " + Id + " не найдена", 404);
            } catch (InvalidTaskIdException e) {
                writeResponse(exchange, "Задача с Id = " + Id + " не найдена", 404);
            }
        }

        private void handleGetSubTaskById(HttpExchange exchange, int Id) throws IOException {
            Subtask task;
            try {
                task = manager.getSubTaskById(Id);
                if (!(task==null))
                    writeResponse(exchange, gson.toJson(task), 200);
                else
                    writeResponse(exchange, "Задача с Id = " + Id + " не найдена", 404);
            } catch (InvalidTaskIdException e) {
                writeResponse(exchange, "Задача с Id = " + Id + " не найдена", 404);
            }
        }

        private void handleGetEpicTaskById(HttpExchange exchange, int Id) throws IOException {
            Epic task;
            try {
                task = manager.getEpicById(Id);
                if (!(task==null))
                    writeResponse(exchange, gson.toJson(task), 200);
                else
                    writeResponse(exchange, "Задача с Id = " + Id + " не найдена", 404);
            } catch (InvalidTaskIdException e) {
                writeResponse(exchange, "Задача с Id = " + Id + " не найдена", 404);
            }
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            List<Task> history = manager.getHistory();
            if (history == null) {
                history = new ArrayList<>();
            }
            writeResponse(exchange, gson.toJson(history), 200);
        }

        private void handleGetPrioritized(HttpExchange exchange) throws IOException {
            List<Task> prioretised = manager.getPrioritizedTasks();
            writeResponse(exchange, gson.toJson(prioretised), 200);
        }

        private void handleDeleteTasks(HttpExchange exchange) throws IOException {
            manager.deleteAllTasks();
            writeResponse(exchange, "Все таски удалены", 200);
        }

        private void handleDeleteSubTasks(HttpExchange exchange) throws IOException {
            manager.deleteAllSubTasks();
            writeResponse(exchange, "Все сабтаски удалены", 200);
        }

        private void handleDeleteEpicTasks(HttpExchange exchange) throws IOException {
            manager.deleteAllEpics();
            writeResponse(exchange, "Все эпики удалены", 200);
        }

        private void handleDeleteTaskById(HttpExchange exchange, int Id) throws IOException {
            int deleteResult = manager.deleteTaskById(Id);
            if (deleteResult == 1) {
                writeResponse(exchange, "Задача с Id = " + Id + " удалена.", 200);
            } else {
                writeResponse(exchange, "Задача с Id = " + Id + " не найдена.", 404);
            }
        }

        private void handlePostTask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (!jsonElement.isJsonObject()) {
                writeResponse(exchange, "Ожидался JSON", 400);
                return;
            }

            Task t = gson.fromJson(body, Task.class);
            List<String> errors = new ArrayList<>();

            if (t.getTaskName() == null) {
                errors.add("Отсутствует обязательный параметр name");
            }

            if (t.getTaskDescription() == null) {
                errors.add("Отсутствует обязательный параметр description");
            }

            if (t.getStartTime() == null && t.getDuration() != 0) {
                errors.add("Была указанна продолжительности (duration), но отсутствует параметр startTime");
            }

            if (errors.size() > 0) {
                writeResponse(exchange, String.join("\n", errors), 400);
                return;
            }

            try {
                Task task = new Task(t.getTaskName(), t.getTaskDescription(), t.getStartTime(), t.getDuration());
                manager.createTask(task);
                writeResponse(exchange, gson.toJson(task), 200);
            } catch (TimeCrossException e) {
                writeResponse(exchange, e.getMessage(), 400);
            }
        }

        private void handlePostSubTask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (!jsonElement.isJsonObject()) {
                writeResponse(exchange, "Неверный формат JSON", 400);
                return;
            }

            Subtask st = gson.fromJson(body, Subtask.class);
            int epicId = st.getEpicId();

            List<String> errors = new ArrayList<>();

            if (epicId == 0) {
                errors.add("Отсутствует обязательный параметр epicTaskId или он равен 0");
            }

            if (st.getTaskName() == null) {
                errors.add("Отсутствует обязательный параметр name");
            }

            if (st.getTaskDescription() == null) {
                errors.add("Отсутствует обязательный параметр description");
            }

            if (st.getStartTime() == null && st.getDuration() != 0) {
                errors.add("Была указанна продолжительности (duration), но отсутствует параметр startTime");
            }

            if (errors.size() > 0) {
                writeResponse(exchange, String.join("\n", errors), 400);
                return;
            }

            try {
                Subtask newSubTask = new Subtask(st.getTaskName(), st.getTaskDescription(), epicId, st.getStartTime(),
                        st.getDuration());
                manager.createSubTask(newSubTask);
                writeResponse(exchange, gson.toJson(newSubTask), 200);
            } catch (TimeCrossException | InvalidTaskIdException e) {
                writeResponse(exchange, e.getMessage(), 400);
            }
        }

        private void handlePostEpicTask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (!jsonElement.isJsonObject()) {
                writeResponse(exchange, "Ожидался JSON", 400);
                return;
            }

            Epic et = gson.fromJson(body, Epic.class);
            List<String> errors = new ArrayList<>();

            if (et.getTaskName() == null) {
                errors.add("Отсутствует обязательный параметр name");
            }

            if (et.getTaskDescription() == null) {
                errors.add("Отсутствует обязательный параметр description");
            }

            if (errors.size() > 0) {
                writeResponse(exchange, String.join("\n", errors), 400);
                return;
            }

            Epic newEpicTask = new Epic(et.getTaskName(), et.getTaskDescription());
            manager.createEpic(newEpicTask);
            writeResponse(exchange, gson.toJson(newEpicTask), 200);
        }

        private void handlePutTask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (!jsonElement.isJsonObject()) {
                writeResponse(exchange, "Ожидался JSON", 400);
                return;
            }

            Task t = gson.fromJson(body, Task.class);

            List<String> errors = new ArrayList<>();

            if (t.getTaskId() == null || t.getTaskId() == 0) {
                errors.add("Отсутствует обязательный параметр id или он равен 0");
            }

            if (t.getTaskStatus() == null) {
                errors.add("Отсутствует обязательный параметр status");
            }

            if (t.getTaskName() == null) {
                errors.add("Отсутствует обязательный параметр name");
            }

            if (t.getTaskDescription() == null) {
                errors.add("Отсутствует обязательный параметр description");
            }

            if (t.getStartTime() == null && t.getDuration() != 0) {
                errors.add("Была указанна продолжительности (duration), но отсутствует параметр startTime");
            }

            if (errors.size() > 0) {
                writeResponse(exchange, String.join("\n", errors), 400);
                return;
            }
            int id = t.getTaskId();
            Task taskToUpd = manager.getAllTasks().stream().filter(task -> task.getTaskId() == id).findFirst()
                    .orElse(null);

            if (taskToUpd == null) {
                writeResponse(exchange, "Задача с id = " + id + " не найдена", 404);
                return;
            }

            Task task = new Task(t.getTaskName(), t.getTaskDescription(), t.getTaskStatus(), id, t.getStartTime(), t.getDuration());

            try {
                manager.updateTask(task);
                writeResponse(exchange, gson.toJson(task), 200);
            } catch (TimeCrossException e) {
                writeResponse(exchange, e.getMessage(), 400);
            }
        }

        private void handlePutSubTask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (!jsonElement.isJsonObject()) {
                writeResponse(exchange, "Ожидался JSON", 400);
                return;
            }

            Subtask st = gson.fromJson(body, Subtask.class);
            //int id = st.getTaskId();
            List<String> errors = new ArrayList<>();

            if (st.getTaskId() == null || st.getTaskId() == 0) {
                errors.add("Отсутствует обязательный параметр id или он равен 0");
            }

            if (st.getTaskName() == null) {
                errors.add("Отсутствует обязательный параметр name");
            }

            if (st.getTaskDescription() == null) {
                errors.add("Отсутствует обязательный параметр description");
            }

            if (st.getStartTime() == null && st.getDuration() != 0) {
                errors.add("Была указанна продолжительности (duration), но отсутствует параметр startTime");
            }

            if (errors.size() > 0) {
                writeResponse(exchange, String.join("\n", errors), 400);
                return;
            }
            int id = st.getTaskId();
            Subtask subTaskToUpd = manager.getAllSubTasks().stream().filter(task -> task.getTaskId() == id).findFirst()
                    .orElse(null);

            if (subTaskToUpd == null) {
                writeResponse(exchange, "Подзадача с id = " + id + " не найдена", 404);
                return;
            }

            Subtask subTask = new Subtask(st.getTaskName(), st.getTaskDescription(), st.getTaskStatus(),
                    subTaskToUpd.getEpicId(), id, st.getStartTime(), st.getDuration());

            try {
                manager.updateSubTask(subTask);
                writeResponse(exchange, gson.toJson(subTask), 200);
            } catch (TimeCrossException | InvalidTaskIdException e) {
                writeResponse(exchange, e.getMessage(), 400);
            }
        }

        private void handlePutEpicTask(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (!jsonElement.isJsonObject()) {
                writeResponse(exchange, "Ожидался JSON", 400);
                return;
            }

            Epic et = gson.fromJson(body, Epic.class);
            //int id = et.getTaskId();

            List<String> errors = new ArrayList<>();

            if (et.getTaskId() == null || et.getTaskId() == 0) {
                errors.add("Отсутствует обязательный параметр id или он равен 0");
            }
            //int id = et.getTaskId();
            if (et.getTaskName() == null) {
                errors.add("Отсутствует обязательный параметр name");
            }

            if (et.getTaskDescription() == null) {
                errors.add("Отсутствует обязательный параметр description");
            }

            if (errors.size() > 0) {
                writeResponse(exchange, String.join("\n", errors), 400);
                return;
            }
            int id = et.getTaskId();
            Epic epicToUpd = manager.getAllEpics().stream().filter(e -> e.getTaskId() == id).findFirst().orElse(null);
            if (epicToUpd == null) {
                writeResponse(exchange, "Эпик задача с id = " + id + " не найдена", 404);
                return;
            }

            manager.updateEpic(new Epic(et.getTaskName(), et.getTaskDescription(), id));
            writeResponse(exchange, gson.toJson(epicToUpd), 200);
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, responseString.length());
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }

        private Endpoints getEndpoint(String method, String path, boolean haveParams) {
            String[] pathParts = path.split("/");

            if (method.equals("GET") && pathParts.length == 3) {
                switch (pathParts[2]) {
                    case "task":
                        if (haveParams) {
                            return Endpoints.GET_TASK_BY_ID;
                        } else {
                            return Endpoints.GET_TASKS;
                        }
                    case "subtask":
                        if (haveParams) {
                            return Endpoints.GET_SUBTASK_BY_ID;
                        } else {
                            return Endpoints.GET_SUBTASKS;
                        }
                    case "epictask":
                        if (haveParams) {
                            return Endpoints.GET_EPICTASK_BY_ID;
                        } else {
                            return Endpoints.GET_EPICTASKS;
                        }
                    case "history":
                        if (!haveParams) {
                            return Endpoints.GET_HISTORY;
                        }
                    case "prioritized":
                        if (!haveParams) {
                            return Endpoints.GET_PRIORITIZED;
                        }
                    default:
                        return Endpoints.UNKNOWN;
                }
            }
            if (method.equals("POST") && pathParts.length == 3) {
                switch (pathParts[2]) {
                    case "task":
                        return Endpoints.POST_TASK;
                    case "subtask":
                        return Endpoints.POST_SUBTASK;
                    case "epictask":
                        return Endpoints.POST_EPICTASK;
                    default:
                        return Endpoints.UNKNOWN;
                }
            }
            if (method.equals("PUT") && pathParts.length == 3) {
                switch (pathParts[2]) {
                    case "task":
                        return Endpoints.PUT_TASK;
                    case "subtask":
                        return Endpoints.PUT_SUBTASK;
                    case "epictask":
                        return Endpoints.PUT_EPICTASK;
                    default:
                        return Endpoints.UNKNOWN;
                }
            }
            if (method.equals("DELETE") && pathParts.length == 3) {
                switch (pathParts[2]) {
                    case "task":
                        if (haveParams) {
                            return Endpoints.DELETE_TASK_BY_ID;
                        } else {
                            return Endpoints.DELETE_TASKS;
                        }
                    case "subtask":
                        if (haveParams) {
                            return Endpoints.DELETE_SUBTASK_BY_ID;
                        } else {
                            return Endpoints.DELETE_SUBTASKS;
                        }
                    case "epictask":
                        if (haveParams) {
                            return Endpoints.DELETE_EPICTASK_BY_ID;
                        } else {
                            return Endpoints.DELETE_EPICTASKS;
                        }
                    default:
                        return Endpoints.UNKNOWN;
                }
            }

            return Endpoints.UNKNOWN;
        }

        private Optional<Map<String, String>> getParams(String path) {
            if (path == null) {
                return Optional.empty();
            }

            Map<String, String> Params = new HashMap<>();

            String[] params = path.split("&");
            for (String param : params) {
                String[] line = param.split("=");
                if (line.length > 1) {
                    Params.put(line[0], line[1]);
                } else {
                    Params.put(line[0], "");
                }
            }

            return Optional.of(Params);
        }
    }

}
