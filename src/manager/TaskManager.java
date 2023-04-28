package manager;

import java.util.List;

import tasks.Task;
import tasks.Tasks;
import tasks.SubTask;
import tasks.Epic;

public interface TaskManager {

    List<Tasks> getHistory();

    int addNewTask(Task task);

    int addNewEpicTask(Epic epic);

    void addNewSubTask(SubTask subTask);

    void updateTask(int id, Tasks tasks);

    void updateStatusTask(Tasks tasks);

    List<Tasks> getAllTasks();

    void printTask(int taskId);

    void printAllTaskOneEpic(int epicId);

    void printListAllTasks();

    void clearAllTask();

    void removeTask(int id);

    Tasks getTaskById(int id);

    int getTaskByName(String name);
}
