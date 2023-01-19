package Manager;

import java.util.List;
import Tasks.Task;
import Tasks.SubTask;
import Tasks.Epic;
import Tasks.SingleTask;
public interface TaskManager {

    List<Task> getHistory();
    void saveNewTask(SingleTask.ToCreateName singleTaskToCreateName);
    void saveNewEpicTask(Epic.ToCreateEpicTaskName epicToCreateEpicTaskName);
    void saveNewSubTask(SubTask.ToCreateSubTaskName subToCreateSubTaskName, int epicId);
    void updateTask(int id, Task task);
    void updateStatusTask(Task task);
    void updateEpicStatus(SubTask subTask);
    List<Task> getAllTasks();
    void printTask(int taskId);
    void printAllTaskOneEpic(int epicId);
    void printListAllTasks();
    void clearAllTask();
    void removeTask(int id);
    Task getTaskById(int id);
}
