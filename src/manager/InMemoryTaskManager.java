package manager;

import tasks.enums.Status;
import tasks.enums.Type;
import manager.hisory.InMemoryHistoryManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.AbstractTasks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final TaskIdGenerator taskIdGenerator;
    protected final HashMap<Integer, AbstractTasks> taskById;
    protected final InMemoryHistoryManager inMemoryHistoryManager;
    protected final Set<AbstractTasks> prioritizedTasks;
    protected final Set<AbstractTasks> prioritizedAbstractTasksWithoutStartTime;

    public InMemoryTaskManager(InMemoryHistoryManager inMemoryHistoryManager) {
        this.inMemoryHistoryManager = inMemoryHistoryManager;
        this.taskIdGenerator = new TaskIdGenerator();
        this.taskById = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(AbstractTasks::getStartTime));
        this.prioritizedAbstractTasksWithoutStartTime = new TreeSet<>(Comparator.comparing(AbstractTasks::getId));
    }

    @Override
    public List<AbstractTasks> getHistory() {
        List<AbstractTasks> historyTasks = new ArrayList<>();
        if (inMemoryHistoryManager.getHistory() == null) {
            return List.of();
        } else {
            for (AbstractTasks abstractTasks : inMemoryHistoryManager.getHistory()) {
                if (taskById.containsKey(abstractTasks.getId())) {
                    historyTasks.add(taskById.get(abstractTasks.getId()));
                }
            }
        }

        return Collections.unmodifiableList(historyTasks);
    }

    @Override
    public int addNewTask(Task task) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        task.setId(nextFreeId);
        taskById.put(task.getId(), task);
        sortTask(task);
        return nextFreeId;
    }

    @Override
    public int addNewEpicTask(Epic epic) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        epic.setId(nextFreeId);
        taskById.put(epic.getId(), epic);
        sortTask(epic);
        return nextFreeId;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        int nextFreeId = taskIdGenerator.getNextFreeId();
        subTask.setId(nextFreeId);

        taskById.put(subTask.getId(), subTask);
        Epic epic = (Epic) taskById.get(subTask.getEpicId());
        epic.getSubtasks().add(subTask);

        if (subTask.getStartTime() != null && subTask.getDuration() != 0) {
            epic.setStartTime(updateEpicTime(epic));
            epic.setDuration(epic.getSumEndTime());
            prioritizedAbstractTasksWithoutStartTime.remove(epic);
        }
        taskById.put(epic.getId(), epic);
        sortTask(subTask);
        sortTask(epic);
        return nextFreeId;
    }

    private void sortTask(AbstractTasks abstractTasks) {
        if (abstractTasks.getStartTime() != null) {
            if (!prioritizedTasks.contains(abstractTasks)) {
                prioritizedTasks.add(abstractTasks);
            }
        } else {
            prioritizedAbstractTasksWithoutStartTime.add(abstractTasks);
        }
    }

    @Override
    public void updateTask(int id, AbstractTasks abstractTasks) {
        taskById.put(id, abstractTasks);
    }

    @Override
    public void updateStatusTask(AbstractTasks abstractTasks, Status status) {
        Type type = abstractTasks.getType();
        if (Type.SUBTASK != type) {
            Task task = (Task) abstractTasks;
            taskById.put(abstractTasks.getId(), task.withNewStatus(task, status));
        } else {
            updateEpicStatus((SubTask) abstractTasks, status);
        }
    }

    private static Instant updateEpicTime(Epic epic) {
        Instant startTime = null;
        for (SubTask subTask : epic.getSubtasks()) {
            if (startTime == null) {
                startTime = subTask.getStartTime();
            }
            if (subTask.getStartTime() != null) {
                if (startTime.compareTo(subTask.getStartTime()) > 0) {
                    startTime = subTask.getStartTime();
                }
            }
        }
        return startTime.minus(1, ChronoUnit.MILLIS);
    }

    private void updateEpicStatus(SubTask subTask, Status status) {
        taskById.put(subTask.getId(), subTask.withStatus(subTask, status));
        int id = subTask.getEpicId();
        Epic epic = (Epic) taskById.get(id);
        epic.withNewStatusSubTask((SubTask) taskById.get(subTask.getId()));

        if (Status.IN_PROGRESS == status) {
            taskById.put(epic.getId(), epic.withNewStatus(epic, status));
        }
        if (Status.DONE == status) {
            if (epic.viewTasksOnDone()) {
                taskById.put(epic.getId(), epic.withNewStatus(epic, status));
            } else {
                taskById.put(epic.getId(), epic.withNewStatus(epic, Status.IN_PROGRESS));
            }
        }
    }

    @Override
    public List<AbstractTasks> getAllTask() {
        return new ArrayList<>(taskById.values()).stream().filter(task -> task.getType().equals(Type.TASK)).toList();
    }

    @Override
    public List<AbstractTasks> getAllEpic() {
        return new ArrayList<>(taskById.values()).stream().filter(task -> task.getType().equals(Type.EPIC)).toList();
    }

    @Override
    public List<AbstractTasks> getAllSubtask() {
        return new ArrayList<>(taskById.values()).stream().filter(task -> task.getType().equals(Type.SUBTASK)).toList();
    }

    @Override
    public List<AbstractTasks> getAllTasks() {
        if (taskById.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.taskById.values());
    }

    @Override
    public List<SubTask> getAllTaskOneEpic(int epicId) {
        Epic epic = (Epic) taskById.get(epicId);
        System.out.println(epic.toString());
        List<SubTask> tasks = epic.getSubtasks();
        System.out.println(tasks.size());
        return new ArrayList<>(epic.getSubtasks());
    }

    @Override
    public boolean clearAllTask() {
        if (!taskById.isEmpty()) {
            taskById.clear();
            prioritizedAbstractTasksWithoutStartTime.clear();
            prioritizedTasks.clear();
            taskIdGenerator.removeNextFreeId();
            return true;
        } else {
            System.out.println("Удалить не получиться, список пустой.");
        }
        return false;
    }

    @Override
    public boolean removeTask(int id) {
        if (Type.SUBTASK == taskById.get(id).getType()) {
            taskById.remove(id);

        } else if (Type.EPIC == taskById.get(id).getType()) {
            Epic epic = (Epic) taskById.get(id);
            List<SubTask> tasks = epic.getSubtasks();
            Iterator<AbstractTasks> iterator = getAllTasks().iterator();
            int count = 0;
            while (iterator.hasNext()) {
                AbstractTasks task = iterator.next();
                for (AbstractTasks abstractTasks1 : tasks) {
                    if (task.getName().equals(abstractTasks1.getName())) {
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
        return taskById.containsKey(id);
    }

    @Override
    public Integer getTaskByName(String name) {
        Optional<Integer> result =
                taskById.entrySet().stream().filter(entry -> name.equals(entry.getValue().getName()))
                        .map(Map.Entry::getKey).findFirst();

        return result.orElse(null);
    }

    @Override
    public AbstractTasks getTaskById(int id) {
        if (taskById.containsKey(id)) {
            inMemoryHistoryManager.add(taskById.get(id));
            return taskById.get(id);
        } else {
            return null;
        }
    }

    public int sizeTaskById() {
        return taskById.size();
    }

    @Override
    public List<AbstractTasks> getPrioritizedTasks() {
        List<AbstractTasks> tasks = new ArrayList<>();
        tasks.addAll(prioritizedTasks);
        tasks.addAll(prioritizedAbstractTasksWithoutStartTime);
        return tasks;
    }

    public static final class TaskIdGenerator {
        private int nextFreeId = 0;

        public int getNextFreeId() {
            return nextFreeId++;
        }

        public void removeNextFreeId() {
            nextFreeId = 0;
        }
    }
}
