package manager;

import manager.hisory.InMemoryHistoryManager;
import tasks.Epic;
import tasks.SingleTask;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTasksManager(InMemoryHistoryManager inMemoryHistoryManager, Path path) {
        super(inMemoryHistoryManager);
        this.path = path;
    }

    @Override
    public void saveNewTask(SingleTask.ToCreateName singleTaskToCreateName) {
        super.saveNewTask(singleTaskToCreateName);
        save();

    }

    @Override
    public void saveNewEpicTask(Epic.ToCreateEpicTaskName epicToCreateEpicTaskName) {
        super.saveNewEpicTask(epicToCreateEpicTaskName);
        save();
    }

    @Override
    public void saveNewSubTask(SubTask.ToCreateSubTaskName subToCreateSubTaskName, int epicId) {
        super.saveNewSubTask(subToCreateSubTaskName, epicId);
        save();
    }

    @Override
    public Task getTaskById(int id) {
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
    public void updateTask(int id, Task task) {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void updateStatusTask(Task task) {
        super.updateStatusTask(task);
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
            for (Task task : taskById.values()) {
                writer.write(task.toString() + "\n");
            }
            if (!inMemoryHistoryManager.isEmpty()) {
                writer.write("\n" + historyToString(getHistory()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fromString(String value) {
        String[] task = value.split(",");
        switch (task[1]) {
            case "TASK":
                saveNewTask(new SingleTask.ToCreateName(task[2], task[4]));
                break;
            case "EPIC":
                saveNewEpicTask(new Epic.ToCreateEpicTaskName(task[2], task[4]));
                break;
            case "SUBTASK":
                int id = Integer.parseInt(task[5]);
                saveNewSubTask(new SubTask.ToCreateSubTaskName(task[2], task[4]), id);
                break;
        }
    }

    public static String historyToString(List<Task> history) {

        return history.stream().map(Task::getId).map(String::valueOf).collect(Collectors.joining(","));
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

        taskManager.saveNewTask(new SingleTask.ToCreateName("Переезд", "Погрузка всех вещей"));
        taskManager.saveNewEpicTask(new Epic.ToCreateEpicTaskName("Покупки", "Список продуктов"));
        taskManager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Погрузка мебели", "Погрузить диван и шкафы"), 1);
        taskManager.saveNewSubTask(new SubTask.ToCreateSubTaskName("Погрузка вещей", "Погрузить одежду и ковры"), 1);

        System.out.println(taskManager.getTaskById(0));
        System.out.println(taskManager.getTaskById(2));


        TaskManager taskManager2 = loadFromFile(Paths.get("Save_Manager.csv"));
        System.out.println(taskManager2.getHistory());
    }

}
