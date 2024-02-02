package Managers.Server;

import Managers.TaskManager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager));
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

        public TaskHandler(TaskManager manager) {
            this.manager = manager;
            this.gson = new Gson();
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
                    || endpoint.equals(Endpoints.DELETE_EPICTASK_BY_ID)){
                if (params == null || params.get("id") == null) {
                    writeResponse(exchange, "Необходимо передать Id задачи", 400);
                    return;
                }
            }
            try {
                id = Integer.parseInt(params.get("id"));
            }
            catch (NumberFormatException | NullPointerException e) {
                writeResponse(exchange,"Неверный формат Id задачи", 400);
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
                    handleDeleteTaskById(exchange, id);
                    break;
                case DELETE_SUBTASK_BY_ID:
                    handleDeleteSubTaskById(exchange, id);
                    break;
                case DELETE_EPICTASK_BY_ID:
                    handleDeleteEpicTaskById(exchange, id);
                    break;
                case UNKNOWN:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }

        }
        private void handleGetTasks(HttpExchange exchange) {

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
