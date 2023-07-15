package tasks;

import enums.Status;
import exception.FileBackedException;
import manager.FileBackedTasksManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import manager.hisory.HistoryManager;
import manager.hisory.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;

    @BeforeEach
    void addNewManager() {
        this.taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void addNewTask() {
        int taskId = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:01:00.00Z"), 20));
        Task savedTask = (Task) taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(taskManager.getTaskById(taskId), savedTask, "Задачи не совпадают.");

        List<Tasks> tasks = taskManager.getAllTasks();

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
        List<Tasks> history = historyManager.getHistory();
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

        List<Tasks> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskManager.getTaskById(taskId), tasks.get(0), "Задачи не совпадают.");

        int subTaskId = taskManager.addNewSubTask(new SubTask("Погрузка мебели"
                , "Погрузить диван и шкафы", Status.NEW, 0, Instant.parse("2023-06-30T15:01:00.00Z")
                , 35));
        Tasks saveSubTask = taskManager.getTaskById(subTaskId);

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

        List<Tasks> tasks = taskManager.getAllTasks();

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
        Tasks tasks = taskManager.getTaskById(taskId1);

        assertNotNull(taskManager.getTaskById(taskId1), "Задачане найдена");
        assertEquals(tasks, taskManager.getTaskById(taskId1), "Задачи не совпадают");
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
        List<Tasks> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи отсутствуют");

        taskManager.clearAllTask();
        tasks = taskManager.getAllTasks();

        assertTrue(tasks.isEmpty(), "Задачи не удалены.");
    }

    @Test
    void saveToFile() {
        TaskManager taskManager = Managers.backedTaskManager(Paths.get("Save_Manager.csv"));
        taskManager.clearAllTask();
        int taskId1 = taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,
                Instant.parse("2023-06-30T16:11:00.00Z"), 20));
        Task savedTask1 = (Task) taskManager.getTaskById(taskId1);


        try (Reader fileReader = new FileReader("Save_Manager.csv", StandardCharsets.UTF_8)) {
            BufferedReader br = new BufferedReader(fileReader);
            String task = br.lines().skip(1).collect(Collectors.joining(System.lineSeparator()));
            String[] tasksAndIdHistory = task.split("\n");
            br.close();
            assertNotNull(task, "Файл пустой");
            assertEquals(savedTask1.toString(), tasksAndIdHistory[0], "Задачи не совпадают.");
        } catch (IOException e) {
            throw new FileBackedException(e.getMessage());
        }
    }

    @Test
    void loadFromFileTest() throws IOException {
        Path path = Path.of("Save_Manager.csv");
        TaskManager taskManager = FileBackedTasksManager.loadFromFile(path);
        List<Tasks> tasks = taskManager.getAllTasks();
        Reader fileReader = new FileReader("Save_Manager.csv", StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(fileReader);
        String task = br.lines().skip(1).collect(Collectors.joining(System.lineSeparator()));
        fileReader.close();
        br.close();
        String[] tasksAndIdHistory = task.split("\n");

        assertNotNull(taskManager, "Менеджер не вернулся.");
        assertNotNull(tasks, "Задачи не возвращаются");
        assertNotNull(taskManager.getHistory(), "История задач пустая");
        assertEquals(tasksAndIdHistory[0], taskManager.getTaskById(0).toString(), "Задачи не одинаковые");

    }
}