package tasks;

import tasks.enums.Status;
import tasks.enums.Type;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SubTask extends AbstractTasks {
    private Status status;
    private int epicId;

    public SubTask() {
    }

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description);
        this.status = status;
        this.epicId = epicId;
    }

    public SubTask(int id ,String name, String description, Status status, int epicId) {
        super(name, description);
        setId(id);
        this.status = status;
        this.epicId = epicId;
    }
    public SubTask(String name, String description, Status status, int epicId, Instant startTime, int duration) {
        super(name, description, startTime,duration);
        setEndTime(startTime.plus(duration, ChronoUnit.MINUTES));
        this.status = status;
        this.epicId = epicId;
    }

    private SubTask(int id, String name, String description, Status status, int epicId, Instant startTime, int duration){
        super(name, description, startTime,duration);
        setId(id);
        setEndTime(startTime.plus(duration, ChronoUnit.MINUTES));
        this.status = status;
        this.epicId = epicId;
    }


    public SubTask withStatus(SubTask subTask ,Status status) {
        SubTask newSubTask;
        if (subTask.getStartTime() == null&& subTask.getDuration() == 0) {
            newSubTask = withNewStatusWithoutTime(subTask, status);
        } else {
            newSubTask = withNewStatusWithTime(subTask, status);
        }
        return newSubTask;
    }

    private SubTask withNewStatusWithoutTime(SubTask subTask ,Status status) {
        return new SubTask(
                subTask.getId(),
                subTask.getName(),
                subTask.getDescription(),
                status,
                subTask.getEpicId()
        );
    }

    private SubTask withNewStatusWithTime(SubTask subTask ,Status status) {
        return new SubTask(
                subTask.getId(),
                subTask.getName(),
                subTask.getDescription(),
                status,
                subTask.getEpicId(),
                subTask.getStartTime(),
                subTask.getDuration()
        );
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    public final void setStatus(Status status) {
        this.status = status;
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return  getId() + "," +
                getType() + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription() + "," +
                getEpicId();
    }
}