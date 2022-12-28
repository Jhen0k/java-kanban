import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {
    private TaskIdGenerator taskIdGenerator = new TaskIdGenerator();
    HashMap<Integer, Task> taskById ;

    public void saveNewTask(SingleTask singleTask){
        singleTask.setId(taskIdGenerator.getNextFreeId());
        taskById.put(singleTask.getId(), singleTask);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task task : this.taskById.values()) {

            tasks.add(task);
        }
        return tasks;
    }

    public ArrayList<Task> getTaskById(List<Integer> taskIds) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int id : taskIds){
            tasks.add(this.taskById.get(id));
        }
        return tasks;
    }

    public static final class TaskIdGenerator{
        private int nextFreeId = 0;

        public int getNextFreeId() {
            return nextFreeId++;
        }
    }
}
