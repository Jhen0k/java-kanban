import enums.Status;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.addNewTask(new Task("Переезд", " Перевезти вещи", Status.NEW, Instant.now(), 30));
        taskManager.addNewEpicTask(new Epic("Переезд", " Перевезти вещи", Status.NEW));
        taskManager.addNewSubTask(new SubTask("Переезд", " Перевезти вещи", Status.NEW, 1, Instant.now(), 45));

        System.out.println(taskManager.getTaskById(0));
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getTaskById(1));


        taskManager.updateStatusTask(taskManager.getTaskById(0), Status.IN_PROGRESS);
        taskManager.updateStatusTask(taskManager.getTaskById(2), Status.DONE);

        System.out.println(taskManager.getTaskById(0));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getTaskById(1));


    }
}
