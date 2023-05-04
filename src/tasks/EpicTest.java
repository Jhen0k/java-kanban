package tasks;

import enums.Status;
import manager.Managers;
import manager.TaskManager;
import manager.hisory.HistoryManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private static TaskManager taskManager;
    private static HistoryManager historyManager;
    private static Task task;
    private static Epic epic;
    private static SubTask subTask;

    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        epic = new Epic("Test addNewTask", "Test addNewTask description", Status.NEW);
        subTask = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW, 1);
        subTask = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW, 1);
        subTask = new SubTask("Test addNewTask", "Test addNewTask description", Status.NEW, 1);

    }

    @Test
    void addNewTask() {
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = (Task) taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Tasks> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Tasks> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    void addNewEpic() {
        List<SubTask> subTasks = epic.getSubtasks();
        assertNotNull(subTasks, "Задачи на возвращаются.");
        
    }

}