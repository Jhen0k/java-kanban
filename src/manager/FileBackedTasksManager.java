package manager;

import enums.Status;
import exception.FileBackedException;
import manager.hisory.InMemoryHistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.AbstractTasks;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    public int addNewSubTask(SubTask subTask) {
        super.addNewSubTask(subTask);
        save();
        return subTask.getId();
    }

    @Override
    public AbstractTasks getTaskById(int id) {
        super.inMemoryHistoryManager.add(taskById.get(id));
        save();
        return super.getTaskById(id);
    }

    @Override
    public int sizeTaskById() {
        return super.sizeTaskById();
    }

    @Override
    public void printAllTaskOneEpic(int epicId) {
        save();
        super.printAllTaskOneEpic(epicId);
    }

    @Override
    public void updateTask(int id, AbstractTasks abstractTasks) {
        super.updateTask(id, abstractTasks);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void updateStatusTask(AbstractTasks abstractTasks, Status status) {
        super.updateStatusTask(abstractTasks, status);
        save();
    }

    @Override
    public void clearAllTask() {
        super.clearAllTask();
        save();
    }

    public void save() {

        try (Writer writer = new FileWriter(path.toFile(), StandardCharsets.UTF_8, false)) {
            writer.write("id,type,name,status,description,epic\n");
            for (AbstractTasks abstractTasks : taskById.values()) {
                writer.write(abstractTasks.toString() + "\n");
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
        final String NAME = task[2];
        final String DESCRIPTION = task[4];
        final Status STATUS = Status.valueOf(task[3]);
        switch (task[1]) {
            case "TASK" -> addNewTask(new Task(NAME, DESCRIPTION, STATUS));
            case "EPIC" -> addNewEpicTask(new Epic(NAME, DESCRIPTION, STATUS));
            case "SUBTASK" -> {
                final int ID = Integer.parseInt(task[5]);
                addNewSubTask(new SubTask(NAME, DESCRIPTION, STATUS, ID));
            }
            default -> System.out.println("Отсутствует тип задачи!");
        }
    }

    public static String historyToString(List<AbstractTasks> history) {

        return history.stream().map(AbstractTasks::getId).map(String::valueOf).collect(Collectors.joining(","));
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
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(new InMemoryHistoryManager(), path);
        Reader fileReader = new FileReader(path.toFile(), StandardCharsets.UTF_8);
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
        if (tasksAndIdHistory[tasksAndIdHistory.length - 1 ].length() < 10) {
            for (int id : historyFromString(tasksAndIdHistory[tasksAndIdHistory.length - 1])) {
                fileBackedTasksManager.getTaskById(id);
            }
            br.close();
        }
        return fileBackedTasksManager;
    }


    public static void main(String[] args) {
        TaskManager taskManager = Managers.backedTaskManager(Paths.get("Save_Manager.csv"));

        taskManager.addNewTask(new Task("Отъезд", "Погрузка всех вещей", Status.NEW));                     // 0
        taskManager.addNewTask(new Task("Переезд", "Погрузка всех вещей", Status.NEW,                      // 1
                Instant.parse("2023-06-30T16:01:00.00Z"), 30));
        taskManager.addNewEpicTask(new Epic("Покупки", "Список продуктов", Status.NEW));                   // 2
        taskManager.addNewSubTask(new SubTask("Погрузка мебели", "Погрузить диван и шкафы", Status.NEW,    // 3
                2, Instant.parse("2023-06-30T15:01:00.00Z"), 30));
        taskManager.addNewSubTask(new SubTask("Погрузка вещей", "Погрузить одежду и ковры", Status.NEW,    // 4
                2, Instant.parse("2023-06-30T15:31:00.00Z"), 45));
        taskManager.addNewTask(new Task("Погрузка", "Погрузка всей мебели", Status.NEW,                    // 5
                Instant.parse("2023-06-30T16:01:00.00Z"), 30));

        System.out.println(taskManager.getTaskById(0).getStartTime());
        System.out.println(taskManager.getTaskById(3).getStartTime());


        try {
            TaskManager taskManager2 = loadFromFile(Paths.get("Save_Manager.csv"));
            System.out.println(taskManager2.getPrioritizedTasks());
            System.out.println(taskManager2.getHistory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
