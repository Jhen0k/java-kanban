package manager;

import manager.hisory.InMemoryHistoryManager;
import tasks.Task;
import tasks.SingleTask;
import tasks.SubTask;
import tasks.Epic;
import Enum.Status;
import Enum.Type;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int taskId = 0;

    private final TaskIdGenerator taskIdGenerator;

    protected final HashMap<Integer, Task> taskById;

    private final List<Integer> historyTaskIds = new ArrayList<>();

    protected final InMemoryHistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager(InMemoryHistoryManager inMemoryHistoryManager) {
        this.inMemoryHistoryManager = inMemoryHistoryManager;
        this.taskIdGenerator = new TaskIdGenerator();
        this.taskById = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyTasks = new ArrayList<>();
        if (inMemoryHistoryManager.getHistory() == null) {
            return null;
        } else {
            for (Task task : inMemoryHistoryManager.getHistory()) {
                if (taskById.containsKey(task.getId())) {
                    historyTasks.add(taskById.get(task.getId()));
                }
            }
        }

        return Collections.unmodifiableList(historyTasks);
    }

    @Override
    public void saveNewTask(SingleTask.ToCreateName singleTaskToCreateName) {
        int nextFreeId = taskIdGenerator.getNextFreeId();

        SingleTask singleTask = new SingleTask(
                nextFreeId,
                singleTaskToCreateName.getName(),
                singleTaskToCreateName.getDescription(),
                Status.NEW
        );

        taskById.put(singleTask.getId(), singleTask);
        this.taskId = singleTask.getId();
    }

    @Override
    public void saveNewEpicTask(Epic.ToCreateEpicTaskName epicToCreateEpicTaskName) {
        int nextFreeId = taskIdGenerator.getNextFreeId();

        Epic epic = new Epic(
                nextFreeId,
                epicToCreateEpicTaskName.getEpicName(),
                epicToCreateEpicTaskName.getDescription(),
                Status.NEW
        );

        taskById.put(epic.getId(), epic);
        this.taskId = epic.getId();
    }

    @Override
    public void saveNewSubTask(SubTask.ToCreateSubTaskName subToCreateSubTaskName, int epicId) {
        int nextFreeId = taskIdGenerator.getNextFreeId();

        SubTask subTask = new SubTask(
                nextFreeId,
                subToCreateSubTaskName.getSubTaskName(),
                subToCreateSubTaskName.getDescription(),
                Status.NEW,
                (Epic) taskById.get(epicId)
        );

        taskById.put(subTask.getId(), subTask);
        Epic epic = (Epic) taskById.get(epicId);
        epic.getSubtasks().add(subTask);
        this.taskId = subTask.getId();
    }

    @Override
    public void updateTask(int id, Task task) {
        taskById.put(id, task);
    }

    @Override
    public void updateStatusTask(Task task) {
        Type type = (Type) task.getType();
        if (!Type.SUBTASK.equals(type)) {
            taskById.put(task.getId(), task);
        } else {
            updateEpicStatus((SubTask) task);
        }
    }

    private void updateEpicStatus(SubTask subTask) {
        taskById.put(subTask.getId(), subTask);
        int id = subTask.getEpic().getId();
        Epic epic = (Epic) taskById.get(id);

        if (Status.IN_PROGRESS.equals(subTask.getStatus())) {
            updateStatusTask(epic.withNewStatus(subTask.getStatus()));
        } else if (Status.DONE.equals(subTask.getStatus())) {
            if (epic.viewTasksOnDone()) {
                updateStatusTask(epic.withNewStatus(subTask.getStatus()));
            } else {
                updateStatusTask(epic.withNewStatus(Status.IN_PROGRESS));
            }
        }
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Task task : this.taskById.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    @Override
    public void printTask(int taskId) {
        System.out.println(taskById.get(taskId).getName());
    }

    @Override
    public void printAllTaskOneEpic(int epicId) {
        Epic epic = (Epic) taskById.get(epicId);

        for (Task task : epic.getSubtasks()) {
            System.out.println(task.getName());
        }
    }

    @Override
    public void printListAllTasks() {
        for (Task task : getAllTasks()) {
            System.out.print(task.getName() + ", ");
        }
        System.out.print("\n");
    }

    @Override
    public void clearAllTask() {
        taskById.clear();
    }

    @Override
    public void removeTask(int id) {
        if (Type.SUBTASK.equals(taskById.get(id).getType())) {
            SubTask subTask = (SubTask) taskById.get(id);
            subTask.getEpic().removeTask(subTask);
            taskById.remove(id);
        } else if (Type.EPIC.equals(taskById.get(id).getType())) {
            Epic epic = (Epic) taskById.get(id);
            List<SubTask> tasks = epic.getSubtasks();
            Iterator<Task> iterator = getAllTasks().iterator();
            int count = 0;
            while (iterator.hasNext()) {
                Task task = iterator.next();
                for (Task task1 : tasks) {
                    if (task.getName() == task1.getName()) {
                        taskById.remove(count);
                        inMemoryHistoryManager.remove(count);
                    }
                }
                count++;
            }
            epic.clearTask();
            taskById.remove(id);
            inMemoryHistoryManager.remove(id);
        } else {
            taskById.remove(id);
        }
    }

    @Override
    public Task getTaskById(int id) {
        inMemoryHistoryManager.add(taskById.get(id));
        return taskById.get(id);
    }

    public static final class TaskIdGenerator {
        private int nextFreeId = 0;

        public int getNextFreeId() {

            return nextFreeId++;
        }
    }
}
