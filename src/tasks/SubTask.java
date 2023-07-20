package tasks;

import enums.Status;
import enums.Type;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SubTask extends AbstractTasks {
    private final Status status;
    private final int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description);
        this.status = status;
        this.epicId = epicId;
    }
    public SubTask(String name, String description, Status status, int epicId, Instant startTime, int duration) {
        super(name, description, startTime,duration);
        setGetEndTime(startTime.plus(duration, ChronoUnit.MINUTES));
        this.status = status;
        this.epicId = epicId;
    }

    private SubTask(int id, String name, String description, Status status, int epicId, Instant startTime, int duration){
        super(name, description, startTime,duration);
        setId(id);
        setGetEndTime(startTime.plus(duration, ChronoUnit.MINUTES));
        this.status = status;
        this.epicId = epicId;
    }


    public SubTask withStatus(SubTask subTask ,Status status) {
        return new SubTask(
                subTask.getId(),
                subTask.getName(),
                subTask.getDescription(),
                status,
                subTask.epicId,
                subTask.getStartTime(),
                subTask.getDuration()
        );
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
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
