package tasks;
import Enum.Status;
import Enum.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subtasks;
    private final Status status;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description);
        this.subtasks = new ArrayList<>();
        this.status = status;
    }

    public Epic withNewStatus(Status status) {
        return new Epic(
                this.getId(),
                this.getName(),
                this.getDescription(),
                status
        );
    }

    public void removeTask(Task Task) {
        Iterator<SubTask> iterator = subtasks.iterator();
        while (iterator.hasNext()) {
            SubTask count = iterator.next();
            if (Task.getName() == count.getName()) {
                iterator.remove();
            }
        }
    }

    public void clearTask() {
        subtasks.clear();
    }

    public boolean viewTasksOnDone() {
        int count = 0;
        boolean statusEpic = false;
        int size = subtasks.size();
        for (SubTask subTask : subtasks) {
            if (Status.DONE.equals(subTask.getStatus())) {
                count++;
            }
        }
        if (size == count) {
            statusEpic = true;
        }
        return statusEpic;
    }

    public List<SubTask> getSubtasks() {
        return subtasks;
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
                Type.EPIC + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription() + ",";
    }

    public static class ToCreateEpicTaskName {
        private String epicName;
        private String description;

        public ToCreateEpicTaskName(String epicName, String description) {
            this.epicName = epicName;
            this.description = description;
        }

        public String getEpicName() {
            return epicName;
        }

        public String getDescription() {
            return description;
        }
    }

}
