package manager;

import enums.Status;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.AbstractTasks;

import java.util.List;

public interface TaskManager {

    List<AbstractTasks> getHistory();

    int addNewTask(Task task);

    int addNewEpicTask(Epic epic);

    int addNewSubTask(SubTask subTask);

    void updateTask(int id, AbstractTasks abstractTasks);

    void updateStatusTask(AbstractTasks abstractTasks, Status status);

    List<AbstractTasks> getAllTasks();

    void printTask(int taskId);

    void printAllTaskOneEpic(int epicId);

    void printListAllTasks();

    void clearAllTask();

    void removeTask(int id);

    AbstractTasks getTaskById(int id);

    int sizeTaskById();

    List<AbstractTasks> getPrioritizedTasks();

    Integer getTaskByName(String name);
}
