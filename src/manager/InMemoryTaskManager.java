package manager;

import manager.hisory.InMemoryHistoryManager;
import tasks.Task;
import tasks.Tasks;
import tasks.SubTask;
import tasks.Epic;
import enums.Status;
import enums.Type;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final TaskIdGenerator taskIdGenerator;

    protected final HashMap<Integer, Tasks> taskById;

    private final List<Integer> historyTaskIds = new ArrayList<>();

    protected final InMemoryHistoryManager inMemoryHistoryManager;

    protected final TreeSet<Tasks> prioritizedTasks;

    public InMemoryTaskManager(InMemoryHistoryManager inMemoryHistoryManager) {
        this.inMemoryHistoryManager = inMemoryHistoryManager;
        this.taskIdGenerator = new TaskIdGenerator();
        this.taskById = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Tasks::getStartTime));
    }

    @Override
    public List<Tasks> getHistory() {
        List<Tasks> historyTasks = new ArrayList<>();
        if (inMemoryHistoryManager.getHistory() == null) {
            return null;
        } else {
            for (Tasks tasks : inMemoryHistoryManager.getHistory()) {
                if (taskById.containsKey(tasks.getId())) {
                    historyTasks.add(taskById.get(tasks.getId()));
                }
            }
        }

        return Collections.unmodifiableList(historyTasks);
    }

    public int addNewTask(Task task) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        task.setId(nextFreeId);
        taskById.put(task.getId(), task);
        return nextFreeId;
    }

    @Override
    public int addNewEpicTask(Epic epic) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        epic.setId(nextFreeId);
        taskById.put(epic.getId(), epic);
        return nextFreeId;
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        subTask.setId(nextFreeId);

        taskById.put(subTask.getId(), subTask);
        Epic epic = (Epic) taskById.get(subTask.getEpicId());
        epic.setStartTime(subTask.getStartTime());
        epic.getSubtasks().add(subTask);
    }

    @Override
    public void updateTask(int id, Tasks tasks) {
        taskById.put(id, tasks);
    }

    @Override
    public void updateStatusTask(Tasks tasks, Status status) {
        Type type = tasks.getType();
        if (!Type.SUBTASK.equals(type)) {
            Task task = (Task)tasks;
            taskById.put(tasks.getId(), task.withNewStatus(task, status));
        } else {
            updateEpicStatus((SubTask) tasks, status);
        }
    }

    private void updateEpicStatus(SubTask subTask, Status status) {
        taskById.put(subTask.getId(), subTask.withStatus(subTask ,status));
        int id = subTask.getEpicId();
        Epic epic = (Epic) taskById.get(id);
        epic.withNewStatusSubTask((SubTask) taskById.get(subTask.getId()));

        if (Status.IN_PROGRESS.equals(status)) {
            taskById.put(epic.getId(), epic.withNewStatus(epic, status));
        } else if (Status.DONE.equals(status)) {
            if (epic.viewTasksOnDone()) {
                taskById.put(epic.getId(), epic.withNewStatus(epic, status));
            } else {
                taskById.put(epic.getId(), epic.withNewStatus(epic, Status.IN_PROGRESS));
            }
        }
    }

    @Override
    public List<Tasks> getAllTasks() {
        return new ArrayList<>(this.taskById.values());
    }

    @Override
    public void printTask(int taskId) {
        System.out.println(taskById.get(taskId).getName());
    }

    @Override
    public void printAllTaskOneEpic(int epicId) {
        Epic epic = (Epic) taskById.get(epicId);

        for (Tasks tasks : epic.getSubtasks()) {
            System.out.println(tasks.getName());
        }
    }

    @Override
    public void printListAllTasks() {
        for (Tasks tasks : getAllTasks()) {
            System.out.print(tasks.getName() + ", ");
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
            taskById.remove(id);
        } else if (Type.EPIC.equals(taskById.get(id).getType())) {
            Epic epic = (Epic) taskById.get(id);
            List<SubTask> tasks = epic.getSubtasks();
            Iterator<Tasks> iterator = getAllTasks().iterator();
            int count = 0;
            while (iterator.hasNext()) {
                Tasks task = iterator.next();
                for (Tasks tasks1 : tasks) {
                    if (task.getName().equals(tasks1.getName())) {
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
    public int getTaskByName(String name) {
        Optional<Integer> result =
                taskById.entrySet().stream().filter(entry -> name.equals(entry.getValue().getName()))
                        .map(Map.Entry::getKey).findFirst();
        return result.get();
    }

    @Override
    public Tasks getTaskById(int id) {
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
