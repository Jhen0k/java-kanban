package manager;

import enums.Status;
import exception.FileBackedException;
import manager.hisory.InMemoryHistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.Tasks;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTasksManager(InMemoryHistoryManager inMemoryHistoryManager, Path path) {
        super(inMemoryHistoryManager);
        this.path = path;
    }

    @Override
    public int addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addNewEpicTask(Epic epic) {
        super.addNewEpicTask(epic);
        save();
        return epic.getId();
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        super.addNewSubTask(subTask);
        save();
    }

    @Override
    public Tasks getTaskById(int id) {
        super.inMemoryHistoryManager.add(taskById.get(id));
        save();
        return super.getTaskById(id);
    }

    @Override
    public void printAllTaskOneEpic(int epicId) {
        save();
        super.printAllTaskOneEpic(epicId);
    }

    @Override
    public void updateTask(int id, Tasks tasks) {
        super.updateTask(id, tasks);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void updateStatusTask(Tasks tasks) {
        super.updateStatusTask(tasks);
        save();
    }

    @Override
    public void clearAllTask() {
        super.clearAllTask();
        save();
    }

    public void save() {

        try (Writer writer = new FileWriter(path.toFile(), Charset.forName(
                "CP1251"), false)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Tasks tasks : taskById.values()) {
                writer.write(tasks.toString() + "\n");
            }
            if (!inMemoryHistoryManager.isEmpty()) {
                writer.write("\n" + historyToString(getHistory()));
            }
        } catch (IOException e) {
            throw new FileBackedException(e.getMessage());
        }
    }

    public void fromString(String value) {
        String[] task = value.split(",");
        switch (task[1]) {
            case "TASK" -> addNewTask(new Task(task[2], task[4], Status.valueOf(task[3])));
            case "EPIC" -> addNewEpicTask(new Epic(task[2], task[4], Status.valueOf(task[3])));
            case "SUBTASK" -> {
                int id = Integer.parseInt(task[5]);
                addNewSubTask(new SubTask(task[2], task[4], Status.valueOf(task[3]), id));
            }
            default -> System.out.println("Отсутствует тип задачи!");
        }
    }

    public static String historyToString(List<Tasks> history) {

        return history.stream().map(Tasks::getId).map(String::valueOf).collect(Collectors.joining(","));
    }


    public static List<Integer> historyFromString(String value) {
        List<Integer> id = new ArrayList<>();
        String[] tasksId = value.split(",");
        for (String taskId : tasksId) {
            id.add(Integer.parseInt(taskId));
        }
        return id;
    }

    public static FileBackedTasksManager loadFromFile(Path path) throws IOException {
        FileBackedTasksManager fileBackedTasksManager = (FileBackedTasksManager) Managers.backedTaskManager(path);
        Reader fileReader = new FileReader(path.toFile(), Charset.forName("CP1251"));
        BufferedReader br = new BufferedReader((fileReader));
        String task = br.lines().skip(1).collect(Collectors.joining(System.lineSeparator()));
        String[] tasksAndIdHistory = task.split("\n");
        for (String line : tasksAndIdHistory) {
            if (line.length() > 10) {
                fileBackedTasksManager.fromString(line);
            } else {
                break;
            }
        }
        for (int id : historyFromString(tasksAndIdHistory[tasksAndIdHistory.length - 1])) {
            fileBackedTasksManager.getTaskById(id);
        }
        br.close();
        return fileBackedTasksManager;
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.backedTaskManager(Paths.get("Save_Manager.csv"));

        taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW));
        taskManager.addNewEpicTask(new Epic("Покупки", "Список продуктов", Status.NEW));
        taskManager.addNewSubTask(new SubTask("Погрузка мебели", "Погрузить диван и шкафы", Status.NEW,  1));
        taskManager.addNewSubTask(new SubTask("Погрузка вещей", "Погрузить одежду и ковры", Status.NEW, 1));

        System.out.println(taskManager.getTaskById(0));
        System.out.println(taskManager.getTaskById(2));


        TaskManager taskManager2 = loadFromFile(Paths.get("Save_Manager.csv"));
        System.out.println(taskManager2.getHistory());
    }

}
