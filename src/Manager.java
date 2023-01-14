import java.util.*;

public class Manager implements GetAllTaskAndId{
    private final TaskIdGenerator taskIdGenerator;

    private final HashMap<Integer, Task> taskById;

    public Manager() {
        this.taskIdGenerator = new TaskIdGenerator();
        this.taskById = new HashMap<>();
    }

    public void saveNewTask(SingleTask.ToCreateName singleTaskToCreateName) {
        int nextFreeId = taskIdGenerator.getNextFreeId();

        SingleTask singleTask = new SingleTask(
                nextFreeId,
                singleTaskToCreateName.getName(),
                singleTaskToCreateName.getDescription(),
                Status.NEW
        );

        taskById.put(singleTask.getId(), singleTask);
    }

    public void saveNewEpicTask(Epic.ToCreateEpicTaskName epicToCreateEpicTaskName) {
        int nextFreeId = taskIdGenerator.getNextFreeId();

        Epic epic = new Epic(
                nextFreeId,
                epicToCreateEpicTaskName.getEpicName(),
                epicToCreateEpicTaskName.getDescription(),
                Status.NEW
        );

        taskById.put(epic.getId(), epic);
    }

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
    }

    public void updateTask(int id, Task task) {
        taskById.put(id, task);
    }

    public void updateStatusTask(Task task) {
        Type type = (Type) task.getType();
        if (!Type.SUB.equals(type)) {
            taskById.put(task.getId(), task);
        } else {
            updateEpicStatus((SubTask) task);
        }

    }

    public void updateEpicStatus(SubTask subTask) {
        taskById.put(subTask.getId(), subTask);
        int id = subTask.getEpic().getId();
        Epic epic = (Epic) taskById.get(id);
        Status status = subTask.getStatus();

        if (Status.IN_PROGRESS.equals(status)) {
            updateStatusTask(epic.withNewStatus(status));
        } else if (Status.DONE.equals(status)) {
            if (epic.viewTasksOnDone()) {
                updateStatusTask(epic.withNewStatus(status));
            } else {
                updateStatusTask(epic.withNewStatus(Status.IN_PROGRESS));
            }
        }
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : this.taskById.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    public void printTask(int taskId) {
        System.out.println(taskById.get(taskId).getName());
    }

    public void printAllTaskOneEpic(int epicId) {
        Epic epic = (Epic) taskById.get(epicId);

        for (Task task : epic.getSubtasks()) {
            System.out.println(task.getName());
        }
    }

    public void printListAllTasks() {
        for (Task task : getAllTasks()) {
            System.out.print(task.getName() + ", ");
        }
        System.out.print("\n");
    }

    public void clearAllTask() {
        taskById.clear();
    }

    public void removeTask(int id) {
        if (Type.SUB.equals(taskById.get(id).getType())) {
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
                    }

                }
                count++;
            }
            epic.clearTask();
            taskById.remove(id);
        } else {
            taskById.remove(id);
        }
    }

    public Task getTaskById(int id) {
        return taskById.get(id);
    }

    public static final class TaskIdGenerator {
        private int nextFreeId = 0;

        public int getNextFreeId() {

            return nextFreeId++;
        }
    }

}
