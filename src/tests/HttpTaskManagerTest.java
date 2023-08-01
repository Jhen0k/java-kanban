package tests;

import adapters.CustomGson;
import com.google.gson.Gson;
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
    void addNewManager() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении состояния на KVServer");
        }
        this.taskManager = new HttpTaskManager();
    }

    @AfterEach
    void stopServers()  {
        httpTaskServer.stop();
        kvServer.stop();
    }

    private void klientSave(String key, String value) {
        try {
            String uriString = String.format("http://%s:%d/%s", host, port, key);
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI(uriString);
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(value)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении состояния на KVServer");
        }
    }

    private String klientLoad(String key, String id) {
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

    @Test
    void addNewJsonTask() throws IOException {
        String task = "{\"name\":\"Заезд\",\"description\":\"Погрузка вещей.\",\"status\":\"NEW\"}";
        Task taskGson = gson.fromJson(task, Task.class);
        klientSave("tasks/task", task);
        Task savedTask = gson.fromJson(klientLoad("tasks/task" ,"0"), Task.class);

        assertNotNull(taskGson, "Задача не десериализована.");
        assertNotNull(savedTask, "Задача не десериализована.");
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
        System.out.println(taskManager.getAllTasks());
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
