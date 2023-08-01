package tasks;

import tasks.enums.Status;
import tasks.enums.Type;

import java.util.ArrayList;
import java.util.List;

public class Epic extends AbstractTasks {
    private List<SubTask> subtasks;
    private Status status;

    public Epic() {
    }

    public Epic(String name, String description, Status status) {
        super(name, description);
        this.subtasks = new ArrayList<>();
        this.status = status;
    }

    private Epic(int id, String name, String description, Status status, List<SubTask> subtasks) {
        super(name, description);
        setId(id);
        this.subtasks = subtasks;
        this.status = status;
    }

    public Epic withNewStatus(Epic epic, Status status) {
        return new Epic(
                epic.getId(),
                epic.getName(),
                epic.getDescription(),
                status,
                epic.getSubtasks()
        );
    }

    public void removeTask(AbstractTasks abstractTasks) {
        subtasks.removeIf(count -> abstractTasks.getName().equals(count.getName()));
    }

    public void clearTask() {
        subtasks.clear();
    }
    public int getSumEndTime() {

        int sum = 0;

        for (SubTask subTask : subtasks) {

            sum = sum + subTask.getDuration();

        }

        return sum;

    }


    public boolean viewTasksOnDone() {
        int count = 0;
        boolean statusEpic = false;
        int size = subtasks.size();
        for (SubTask subTask : subtasks) {
            if (Status.DONE == subTask.getStatus()) {
                count++;
            }
        }
        if (size == count) {
            statusEpic = true;
        }
        return statusEpic;
    }

    public void withNewStatusSubTask(SubTask subTask) {
        int count = 0;
        for (SubTask subTask1 : subtasks) {
            if (subTask.getId().equals(subTask1.getId())) {
                subtasks.set(count, subTask);
            }
            count++;
        }
    }

    public void setSubtasks(List<SubTask> subtasks) {
        this.subtasks = subtasks;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<SubTask> getSubtasks() {
        return this.subtasks;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public String toString() {
        return  getId() + "," +
                getType() + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription();
    }

}