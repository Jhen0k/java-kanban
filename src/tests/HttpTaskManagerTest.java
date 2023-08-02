package tests;

import adapters.CustomGson;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import kvserver.KVServer;
import manager.Managers;
import manager.TaskManager;
import manager.hisory.HistoryManager;
import manager.http.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.AbstractTasks;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.enums.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {

    private final int port = 8080;
    private final String host = "localhost";
    Gson gson = new CustomGson().customGson();
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;

    public HttpTaskManagerTest() {
    }

    @BeforeEach
    void startServers() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
            deleteRequest("");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении состояния на KVServer");
        }
        this.taskManager = new HttpTaskManager();
    }

    @AfterEach
    void stopServers() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    private String getRequest(String key, String id) {
        String responseStr;
        try {
            String uriString = String.format("http://%s:%d/%s/?id=%s", host, port, key, id);
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI(uriString);
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseStr = response.body();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке состояния из KVServer");
        }
        return responseStr;
    }

    private String postRequest(String key, String value) {
        String responseStr;
        try {
            String uriString = String.format("http://%s:%d/%s", host, port, key);
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI(uriString);
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(value)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseStr = response.body();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении состояния на KVServer");
        }
        return responseStr;
    }

    private void deleteRequest(String id) {
        String idTask = "?id=" + id;
        if (id.isEmpty()) {
            idTask = "";
        }
        try {
            String uriString = String.format("http://%s:%d/tasks/task/%s", host, port, idTask);
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI(uriString);
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке состояния из KVServer");
        }
    }

    @Test
    void methodAddTask() {
        String taskGson1 = gson.toJson(new Task("Отъезд", "Погрузка всех вещей", Status.NEW));
        Task task = gson.fromJson(taskGson1, Task.class);
        String[] response = postRequest("tasks/task", taskGson1).split(":"); // Сохраняем задачу в KVServer
        String taskGson2 = getRequest("tasks/task", response[1]);    // Получаем задачу от KVServer по id

        String listGson = getRequest("tasks", "");

        JsonElement jsonElement = JsonParser.parseString(listGson);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertNotNull(taskGson1, "Задача не сериализована.");
        assertNotNull(task, "Задача не десериализована.");

        assertNotNull(gson.fromJson(getRequest("tasks/task", response[1]), Task.class), "Задача не найдена");
        assertNotNull(listGson, "Задачи на возвращаются.");
        assertEquals(1, jsonArray.size(), "Неверное количество задач.");
        assertEquals(taskGson1, taskGson2, "Задачи не совпадают.");

    }

    @Test
    void methodAddEpic() {
        JsonElement jsonElement1 = JsonParser.parseString(getRequest("tasks", ""));
        JsonArray jsonArray1 = jsonElement1.getAsJsonArray();
        System.out.println(jsonArray1);
        String taskGson1 = gson.toJson(new Epic("Отъезд", "Погрузка всех вещей", Status.NEW));
        Epic epic = gson.fromJson(taskGson1, Epic.class);
        String[] response = postRequest("tasks/epic", taskGson1).split(":"); // Сохраняем задачу в KVServer
        String taskGson2 = getRequest("tasks/task", response[1]);    // Получаем задачу от KVServera по id

        String listGson = getRequest("tasks", "");

        JsonElement jsonElement = JsonParser.parseString(listGson);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertNotNull(taskGson1, "Задача не сериализована.");
        assertNotNull(epic, "Задача не десериализована.");

        assertNotNull(gson.fromJson(getRequest("tasks/task", response[1]), Epic.class), "Задача не найдена");
        assertNotNull(listGson, "Задачи на возвращаются.");
        System.out.println(jsonArray);
        assertEquals(1, jsonArray.size(), "Неверное количество задач.");
        assertEquals(taskGson1, taskGson2, "Задачи не совпадают.");
    }

    @Test
    void methodAddSubTask() {
        String taskGson1 = gson.toJson(new Epic("Отъезд", "Погрузка всех вещей", Status.NEW));
        SubTask subTask1 = new SubTask("Отъезд", "Погрузка всех вещей", Status.NEW, 0);
        String taskGson2 = gson.toJson(subTask1);
        SubTask subTask2 = gson.fromJson(taskGson2, SubTask.class);
        postRequest("tasks/epic", taskGson1); // Сохраняем epic задачу в KVServer
        String[] response = postRequest("tasks/subtask", taskGson2).split(":"); // Сохраняем задачу в KVServer
        String taskGson3 = getRequest("tasks/task", response[1]);    // Получаем задачу от KVServer по id
        subTask1.setId(1);
        String taskGson4 = gson.toJson(subTask1);

        String listGson = getRequest("tasks", "");

        JsonElement jsonElement = JsonParser.parseString(listGson);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertNotNull(taskGson1, "Задача не сериализована.");
        assertNotNull(subTask2, "Задача не десериализована.");

        assertNotNull(gson.fromJson(getRequest("tasks/task", response[1]), SubTask.class), "Задача не найдена");
        assertNotNull(listGson, "Задачи на возвращаются.");
        assertEquals(2, jsonArray.size(), "Неверное количество задач.");
        System.out.println(taskGson2);
        System.out.println(taskGson3);
        assertEquals(taskGson4, taskGson3, "Задачи не совпадают.");
    }

    @Test
    void methodAddHistory() {
        String taskGson1 = gson.toJson(new Task("Отъезд", "Погрузка всех вещей", Status.NEW));
        postRequest("tasks/task", taskGson1);  // Сохраняем задачу в KVServer
        getRequest("tasks/task", "0");

        String history = getRequest("tasks/history", "");

        JsonElement jsonElement = JsonParser.parseString(history);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertNotNull(jsonArray, "История не пустая.");
        assertEquals(1, jsonArray.size(), "Неверное количество задач.");
    }

    @Test
    void methodUpdateTaskStatus() {
        String taskGson = gson.toJson(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        postRequest("tasks/task", taskGson);
        Task task = gson.fromJson(getRequest("tasks/task", "0"), Task.class);
        Status status = task.getStatus();
        postRequest("tasks/task?taskid=0&status=IN_PROGRESS", "");
        Task task2 = gson.fromJson(getRequest("tasks/task", "0"), Task.class);

        assertEquals(Status.NEW, status, "Статус не установлен");
        assertEquals(Status.IN_PROGRESS, task2.getStatus(), "Статус не изменён");

    }

    @Test
    void methodUpdateEpicStatus() {
        String epicGson = gson.toJson(new Epic("Переезд", "Погрузка всех вещей", Status.NEW));
        String subTaskGson = gson.toJson(new SubTask("Погрузка вещей", "Погрузить одежду" +
                " и ковры", Status.NEW, 0, Instant.parse("2023-07-30T15:31:00.00Z"), 45));
        postRequest("tasks/epic", epicGson);
        postRequest("tasks/subtask", subTaskGson);

        Epic epic = gson.fromJson(getRequest("tasks/task", "0"), Epic.class);
        SubTask subTask = gson.fromJson(getRequest("tasks/task", "1"), SubTask.class);

        assertEquals(Status.NEW, epic.getStatus(), "Статусы не совпадают!");
        assertEquals(Status.NEW, subTask.getStatus(), "Статусы не совпадают!");

        postRequest("tasks/task?taskid=1&status=IN_PROGRESS", "");

        Epic epic2 = gson.fromJson(getRequest("tasks/task", "0"), Epic.class);
        SubTask subTask2 = gson.fromJson(getRequest("tasks/task", "1"), SubTask.class);

        assertEquals(Status.IN_PROGRESS, subTask2.getStatus(), "Статус не изменился!");
        assertEquals(Status.IN_PROGRESS, epic2.getStatus(), "Статус не изменился!");

        postRequest("tasks/task?taskid=1&status=DONE", "");

        Epic epic3 = gson.fromJson(getRequest("tasks/task", "0"), Epic.class);
        SubTask subTask3 = gson.fromJson(getRequest("tasks/task", "1"), SubTask.class);

        assertEquals(Status.DONE, subTask3.getStatus(), "Статус не изменился!");
        assertEquals(Status.DONE, epic3.getStatus(), "Статус не изменился!");
    }

    @Test
    void methodSortTaskByTime() {
        String taskGson1 = gson.toJson(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:01:00.00Z"), 20));
        String taskGson2 = gson.toJson(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:01:00.00Z"), 20));
        String taskGson3 = gson.toJson(new Task("Переезд", "Погрузка всех вещей", Status.NEW));

        postRequest("tasks/task", taskGson1);
        postRequest("tasks/task", taskGson2);
        postRequest("tasks/task", taskGson3);

        Task task1 = gson.fromJson(getRequest("tasks/task", "0"), Task.class);
        Task task2 = gson.fromJson(getRequest("tasks/task", "1"), Task.class);
        Task task3 = gson.fromJson(getRequest("tasks/task", "2"), Task.class);

        assertNotNull(task1, "Задача не найдена!");
        assertNotNull(task2, "Задача не найдена!");
        assertNotNull(task3, "Задача не найдена!");

        assertNotNull(task1.getStartTime(), "У задачи не задано время");
        assertNotNull(task2.getStartTime(), "У задачи не задано время");

        String prioritizedTasks = getRequest("tasks/priority", "");

        JsonElement jsonElement = JsonParser.parseString(prioritizedTasks);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        System.out.println(jsonArray);

        assertEquals(2, jsonArray.size(), "Не работает валидация по времени");

    }
    @Test
    void methodRemoveTask() {
        String taskGson1 = gson.toJson(new Task("Отъезд", "Погрузка всех вещей", Status.NEW));
        String taskGson2 = gson.toJson(new Task("Отъезд", "Погрузка всех вещей", Status.NEW));

        postRequest("tasks/task", taskGson1);
        postRequest("tasks/task", taskGson2);

        assertNotNull(getRequest("tasks/task", "0"));
        assertNotNull(getRequest("tasks/task", "1"));

        deleteRequest("0");

        String task = getRequest("tasks/task", "0");
        if (task.equals("null")) {
            task = null;
        }
        assertNull(task , "Задача не удалена!");

        String tasks = getRequest("tasks", "");

        JsonElement jsonElement = JsonParser.parseString(tasks);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(1, jsonArray.size(), "В списке есть задачи.");
    }
    @Test
    void methodClearTasks() {
        String taskGson1 = gson.toJson(new Task("Отъезд", "Погрузка всех вещей", Status.NEW));
        String taskGson2 = gson.toJson(new Task("Отъезд", "Погрузка всех вещей", Status.NEW));

        postRequest("tasks/task", taskGson1);
        postRequest("tasks/task", taskGson2);

        assertNotNull(getRequest("tasks/task", "0"));
        assertNotNull(getRequest("tasks/task", "1"));

        String tasks1 = getRequest("tasks", "");

        JsonElement jsonElement1 = JsonParser.parseString(tasks1);
        JsonArray jsonArray1 = jsonElement1.getAsJsonArray();

        assertEquals(2, jsonArray1.size(), "Задачи не возвращаются!");

        deleteRequest("");

        String tasks2 = getRequest("tasks", "");
        System.out.println(tasks2);
        if (tasks2.equals("[]")) {
            tasks2 = null;
        }

        assertNull(tasks2, "Задачи не удалены!");
    }

    @Test
    void addNewTask() {
        int taskId = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:01:00.00Z"), 20));
        Task savedTask = (Task) taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(taskManager.getTaskById(taskId), savedTask, "Задачи не совпадают.");

        List<AbstractTasks> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskManager.getTaskById(taskId), tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void add() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        int taskId = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW
                , Instant.parse("2023-06-30T16:30:00.00Z"), 30));
        historyManager.add(taskManager.getTaskById(taskId));
        List<AbstractTasks> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addNewEpic() {
        int taskId = taskManager.addNewEpicTask(new Epic("Покупки", "Список продуктов"
                , Status.NEW));
        Epic epic = (Epic) taskManager.getTaskById(taskId);
        Epic savedTEpic = (Epic) taskManager.getTaskById(taskId);

        assertNotNull(savedTEpic, "Задача не найдена.");
        assertEquals(taskManager.getTaskById(taskId), savedTEpic, "Задачи на возвращаются.");

        List<AbstractTasks> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskManager.getTaskById(taskId), tasks.get(0), "Задачи не совпадают.");

        int subTaskId = taskManager.addNewSubTask(new SubTask("Погрузка мебели"
                , "Погрузить диван и шкафы", Status.NEW, 0, Instant.parse("2023-06-30T15:01:00.00Z")
                , 35));
        AbstractTasks saveSubTask = taskManager.getTaskById(subTaskId);

        List<SubTask> subTasks = epic.getSubtasks();

        assertNotNull(saveSubTask, "Задача не найдена.");
        assertEquals(taskManager.getTaskById(subTaskId), saveSubTask, "Задачи на возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
    }

    @Test
    void addSubTask() {
        int taskId = taskManager.addNewEpicTask(new Epic("Покупки", "Список продуктов"
                , Status.NEW));
        int taskId2 = taskManager.addNewSubTask(new SubTask("Погрузка вещей", "Погрузить одежду" +
                " и ковры", Status.NEW, 0, Instant.parse("2023-07-30T15:31:00.00Z"), 45));
        SubTask saveSubTask = (SubTask) taskManager.getTaskById(taskId2);

        assertNotNull(saveSubTask, "Задача не найдена.");
        assertEquals(taskManager.getTaskById(taskId2), saveSubTask, "Задачи не совпадают.");

        List<AbstractTasks> tasks = taskManager.getAllTasks();

        assertNotNull(taskManager.getTaskById(taskId), "Задача не найдена");
        assertNotNull(taskManager.getTaskById(taskId2), "Задача не найдена");
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(taskManager.getTaskById(taskId2), tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void updateTaskStatus() {
        int taskId = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        Task savedTask = (Task) taskManager.getTaskById(taskId);
        Status status = savedTask.getStatus();
        taskManager.updateStatusTask(savedTask, Status.IN_PROGRESS);

        assertEquals(Status.NEW, status, "Статус не установлен");
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(0).getStatus(), "Статус не изменён");
    }

    @Test
    void updateEpicStatus() {
        int taskId = taskManager.addNewEpicTask(new Epic("Покупки", "Список продуктов"
                , Status.NEW));
        int taskId1 = taskManager.addNewSubTask(new SubTask("Погрузка вещей", "Погрузить одежду" +
                " и ковры", Status.NEW, 0, Instant.parse("2023-07-30T15:31:00.00Z"), 45));
        Status statusEpic = taskManager.getTaskById(taskId).getStatus();
        Status statusSubtask = taskManager.getTaskById(taskId1).getStatus();

        assertEquals(Status.NEW, statusEpic, "Статус не задан");
        assertEquals(Status.NEW, statusSubtask, "Статус не задан");

        taskManager.updateStatusTask(taskManager.getTaskById(taskId1), Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(taskId).getStatus(), "Статус не изменился");
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(taskId1).getStatus(), "Статус не изменился");

        taskManager.updateStatusTask(taskManager.getTaskById(taskId1), Status.DONE);

        assertEquals(Status.DONE, taskManager.getTaskById(taskId).getStatus(), "Статус не изменился");
        assertEquals(Status.DONE, taskManager.getTaskById(taskId1).getStatus(), "Статус не изменился");
    }

    @Test
    void sortTask() {
        int taskId1 = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        int taskId2 = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        int taskId3 = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей"
                , Status.NEW));
        Task savedTask1 = (Task) taskManager.getTaskById(taskId1);
        Task savedTask2 = (Task) taskManager.getTaskById(taskId2);
        Task savedTask3 = (Task) taskManager.getTaskById(taskId3);

        assertNotNull(savedTask1, "Задача не найдена.");
        assertNotNull(savedTask2, "Задача не найдена.");
        assertNotNull(savedTask3, "Задача не найдена.");
        assertNotNull(savedTask1.getStartTime(), "У задачи не задано время");
        assertNotNull(savedTask2.getStartTime(), "У задачи не задано время");
        assertNull(savedTask3.getStartTime(), "У задачи задано время");

        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Не работает валидация по времени");
    }

    @Test
    void getTaskById() {
        int taskId1 = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        AbstractTasks abstractTasks = taskManager.getTaskById(taskId1);

        assertNotNull(taskManager.getTaskById(taskId1), "Задачане найдена");
        assertEquals(abstractTasks, taskManager.getTaskById(taskId1), "Задачи не совпадают");
    }

    @Test
    void removeTask() {
        int taskId1 = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        int taskId2 = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        assertNotNull(taskManager.getTaskById(taskId1), "Задача не найдена");
        assertNotNull(taskManager.getTaskById(taskId2), "Задача не найдена");
        assertEquals(2, taskManager.sizeTaskById(), "Задачи не добавлены");

        taskManager.removeTask(taskId1);
        assertNull(taskManager.getTaskById(taskId1), "Задача не удалена");
        assertEquals(1, taskManager.sizeTaskById(), "В списке есть задачи.");
    }

    @Test
    void clearAllTask() {
        taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        List<AbstractTasks> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи отсутствуют");

        taskManager.clearAllTask();
        tasks = taskManager.getAllTasks();

        assertTrue(tasks.isEmpty(), "Задачи не удалены.");
    }
}
