import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import Enum.Status;
import tasks.SubTask;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.saveNewEpicTask(new Epic.ToCreateEpicTaskName("Покупки", "Список продуктов"));
        System.out.println(taskManager.getTaskById(0));
        taskManager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Молочка", "Купить молоко, кефир, сметану, творог"),
                0);
        taskManager.saveNewEpicTask(new Epic.ToCreateEpicTaskName("Переезд",
                "Погрузка всех вещей"));
        taskManager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Погрузка мебели", "Погрузить диван и шкафы"), 2);
        SubTask subTask2 = (SubTask) taskManager.getTaskById(3);
        taskManager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Погрузка вещей", "Погрузить одежду и ковры"), 2);
        SubTask subTask3 = (SubTask) taskManager.getTaskById(4);
        taskManager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Уборать в квартире", "Убрать комнату, убрать зал, " +
                "убрать на кухне"), 2);


        taskManager.printAllTaskOneEpic(2);
        taskManager.removeTask(5);
        taskManager.printAllTaskOneEpic(2);
        taskManager.printListAllTasks();

        System.out.println(taskManager.getTaskById(0));
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getTaskById(3));
        System.out.println(taskManager.getTaskById(4));
        System.out.println(" ");

        taskManager.updateStatusTask(subTask2.withStatus(Status.DONE));
        taskManager.updateStatusTask(subTask3.withStatus(Status.IN_PROGRESS));

        System.out.println(taskManager.getTaskById(0));
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getTaskById(3));
        System.out.println(taskManager.getTaskById(4));

        taskManager.printListAllTasks();
        taskManager.removeTask(0);
        taskManager.printListAllTasks();

        System.out.println(taskManager.getTaskById(2));
        System.out.println("Печать истории" + taskManager.getHistory());


    }
}



