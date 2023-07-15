package manager;

import enums.Status;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.Tasks;

import java.util.List;

public interface TaskManager {

    List<Tasks> getHistory();

    int addNewTask(Task task);

    int addNewEpicTask(Epic epic);

    int addNewSubTask(SubTask subTask);

    void updateTask(int id, Tasks tasks);

    void updateStatusTask(Tasks tasks, Status status);

    List<Tasks> getAllTasks();

    void printTask(int taskId);

    void printAllTaskOneEpic(int epicId);

    void printListAllTasks();

    void clearAllTask();

    void removeTask(int id);

    Tasks getTaskById(int id);

    int sizeTaskById();

    List<Tasks> getPrioritizedTasks();

    int getTaskByName(String name);
}
