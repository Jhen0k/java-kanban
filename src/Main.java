public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        SingleTask singleTask = new SingleTask(null, "Pure task", Status.NEW);
        Epic epic = new Epic(2, "Epic task");
        SubTask subtask = new SubTask(3, "Sub task", Status.NEW, epic);
    }
}
