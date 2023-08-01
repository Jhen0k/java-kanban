package manager;

import tasks.AbstractTasks;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.enums.Status;

import java.util.List;

public interface TaskManager {

    int addNewTask(Task task);

    int addNewEpicTask(Epic epic);

    int addNewSubTask(SubTask subTask);

    void updateTask(int id, AbstractTasks abstractTasks);

    void updateStatusTask(AbstractTasks abstractTasks, Status status);
    List<AbstractTasks> getAllTask();

    List<AbstractTasks> getAllEpic();

    List<AbstractTasks> getAllSubtask();

    List<AbstractTasks> getAllTasks();
    List<SubTask> getAllTaskOneEpic(int epicId);
    List<AbstractTasks> getHistory();
    boolean clearAllTask();

    boolean removeTask(int id);

    AbstractTasks getTaskById(int id);

    int sizeTaskById();

    List<AbstractTasks> getPrioritizedTasks();

    Integer getTaskByName(String name);
}
