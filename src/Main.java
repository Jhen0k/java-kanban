
public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.saveNewEpicTask(new Epic.ToCreateEpicTaskName("Покупки", "Список продуктов"));
        Epic epic1 = (Epic) manager.getTaskById(0);
        manager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Молочка", "Купить молоко, кефир, сметану, творог"),
                0);
        SubTask subTask1 = (SubTask) manager.getTaskById(1);
        manager.saveNewEpicTask(new Epic.ToCreateEpicTaskName("Переезд",
                "Погрузка всех вещей"));
        Epic epic2 = (Epic) manager.getTaskById(2);
        manager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Погрузка мебели", "Погрузить диван и шкафы"), 2);
        SubTask subTask2 = (SubTask) manager.getTaskById(3);
        manager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Погрузка вещей", "Погрузить одежду и ковры"), 2);
        SubTask subTask3 = (SubTask) manager.getTaskById(4);
        manager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Уборать в квартире", "Убрать комнату, убрать зал, " +
                "убрать на кухне"), 2);


        System.out.println(manager.getTaskById(0));
        System.out.println(manager.getTaskById(0));
        manager.printAllTaskOneEpic(2);
        manager.removeTask(5);
        manager.printAllTaskOneEpic(2);
        manager.printListAllTasks();

        System.out.println(manager.getTaskById(0));
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getTaskById(3));
        System.out.println(manager.getTaskById(4));
        System.out.println("");

        manager.updateStatusTask(subTask2.withStatus(Status.DONE));
        manager.updateStatusTask(subTask3.withStatus(Status.IN_PROGRESS));

        System.out.println(manager.getTaskById(0));
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getTaskById(3));
        System.out.println(manager.getTaskById(4));

        manager.printListAllTasks();
        manager.removeTask(0);
        manager.printListAllTasks();


    }
}



