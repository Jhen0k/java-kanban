import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.saveNewEpicTask(new Epic.ToCreateEpicTaskName("Покупки", "Список продуктов"));
        taskManager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Молочка", "Купить молоко, кефир, сметану"), 0);
        taskManager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Погрузка мебели", "Погрузить диван и шкафы"), 0);
        taskManager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Погрузка вещей", "Погрузить одежду и ковры"), 0);
        taskManager.saveNewEpicTask(new Epic.ToCreateEpicTaskName("Переезд", "Погрузка всех вещей"));

        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getTaskById(4));
        System.out.println(taskManager.getTaskById(0));
        System.out.println(taskManager.getTaskById(3));
        System.out.println(taskManager.getTaskById(3));
        System.out.println(taskManager.getTaskById(1));
        System.out.println(" ");
        System.out.println("Печать истории" + taskManager.getHistory());

        System.out.println(" ");
        taskManager.removeTask(3);
        System.out.println("Печать истории" + taskManager.getHistory());

        System.out.println(" ");
        taskManager.removeTask(0);
        System.out.println("Печать истории" + taskManager.getHistory());

    }
}



