package server;

import adapters.CustomGson;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.enums.Type;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpTaskServer {
    private final int PORT = 8080;
    private final TaskManager taskManager;
    private final HttpServer httpServer;
    private final Gson gson;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        gson = new CustomGson().customGson();
        httpServer.createContext("/tasks", this::getAllTasks);
        httpServer.createContext("/tasks/task", this::task);
        httpServer.createContext("/tasks/epic", this::Epic);
        httpServer.createContext("/tasks/subtask", this::Subtask);
        httpServer.createContext("/tasks/subtask/epic", this::subtasksOneEpic);
        httpServer.createContext("/tasks/history", this::getHistory);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        httpServer.start();
        System.out.println("Началась обработка /tasks запроса от клиента.");
    }

    public void stop() {
        System.out.println("Сервер остановлен!");
        httpServer.stop(0);
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        String[] requestPath = exchange.getRequestURI().getPath().split("/");
        try {
            if (requestPath.length == 2) {
                writeResponse(exchange, gson.toJson(taskManager.getAllTasks()), 200);
            }
        } catch (IOException e) {
            writeResponse(exchange, "Произошла ошибка при получении задач.", 404);
        }
    }

    private void task(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Endpoint endpoint = getEndpoint(exchange);

        switch (endpoint) {
            case GET -> {
                if (query == null) {
                    writeResponse(exchange, gson.toJson(taskManager.getAllTask()), 200);
                } else {
                    getTaskById(exchange, query);
                }
            }
            case POST -> addTask(exchange, Type.TASK);
            case DELETE -> {
                if (query == null) {
                    taskManager.clearAllTask();
                    writeResponse(exchange, "Все задачи удалены!", 200);
                } else {
                    String[] queryTask = query.split("=");
                    taskManager.removeTask(Integer.parseInt(queryTask[1]));
                    writeResponse(exchange, "Задача c id " + queryTask[1] + " удалена!", 200);
                }
            }
        }
    }

    private void Epic(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);

        switch (endpoint) {
            case GET -> writeResponse(exchange, gson.toJson(taskManager.getAllEpic()), 200);
            case POST -> addTask(exchange, Type.EPIC);
        }
    }

    private void Subtask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Endpoint endpoint = getEndpoint(exchange);

        switch (endpoint) {
            case GET -> {
                if (query == null) {
                    writeResponse(exchange, gson.toJson(taskManager.getAllSubtask()), 200);
                } else {
                    getTaskById(exchange, query);
                }
            }
            case POST -> addTask(exchange, Type.SUBTASK);
        }
    }

    private void subtasksOneEpic(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Endpoint endpoint = getEndpoint(exchange);

        switch (endpoint) {
            case GET -> {
                if (query != null) {
                    String[] queryTask = query.split("=");
                    Optional<Integer> id = getPostId(queryTask[1]);
                    if (id.isPresent()) {
                        writeResponse(exchange, gson.toJson(taskManager.getAllTaskOneEpic(Integer.parseInt(queryTask[1])))
                                , 200);
                    } else {
                        writeResponse(exchange, "id не является числом!", 400);
                    }
                } else {
                    writeResponse(exchange, "Id epic не указан.", 400);
                }
            }
            case POST -> writeResponse(exchange, "Не верно выбран мнтод", 400);
        }
    }

    private void addTask(HttpExchange exchange, Type type) throws IOException {
        int id = 0;
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        try {
            switch (type) {
                case TASK -> id = taskManager.addNewTask(gson.fromJson(body, Task.class));
                case EPIC -> id = taskManager.addNewEpicTask(gson.fromJson(body, Epic.class));
                case SUBTASK -> id = taskManager.addNewSubTask(gson.fromJson(body, SubTask.class));
                default -> writeResponse(exchange, "Тип задачи не определен.", 400);

            }
            writeResponse(exchange, "Задача добавлена c id:" + id, 200);
        } catch (IOException e) {
            writeResponse(exchange, "Задача не добавлена", 400);
        }
    }

    private void getTaskById(HttpExchange exchange, String query) throws IOException {
        String[] queryTask = query.split("=");
        Optional<Integer> id = getPostId(queryTask[1]);
        if (id.isPresent()) {
            writeResponse(exchange, gson.toJson(taskManager.getTaskById(id.get()))
                    , 200);
        } else writeResponse(exchange, "id не является числом!", 400);
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        try {
            writeResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
        } catch (IOException e) {
            writeResponse(exchange, "История не найдена", 200);
        }

    }

    private Endpoint getEndpoint(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") && pathParts[1].equals("tasks")) {
            return Endpoint.GET;
        }
        if (method.equals("POST") && pathParts[1].equals("tasks")) {
            return Endpoint.POST;
        }
        if (method.equals("DELETE") && pathParts[1].equals("tasks")) {
            return Endpoint.DELETE;
        }
        return Endpoint.UNKNOWN;
    }

    private Optional<Integer> getPostId(String id) {
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }


    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
