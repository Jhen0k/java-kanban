package tasks;

import enums.Status;
import enums.Type;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


public class Task extends AbstractTasks {
    private final Status status;

    public Task(String name, String description, Status status) {
        super(name, description);
        this.status = status;
    }
    public Task(String name, String description, Status status, Instant startTime, int duration) {
        super(name, description, startTime, duration);
        setGetEndTime(startTime.plus(duration, ChronoUnit.MINUTES));
        this.status = status;
    }

    private Task(int id, String name, String description, Status status, Instant startTime, int duration) {
        super(name, description, startTime, duration);
        setId(id);
        setGetEndTime(startTime.plus(duration, ChronoUnit.MINUTES));
        this.status = status;
    }
    public Task withNewStatus(Task task ,Status status) {
        return new Task(
                task.getId(),
                task.getName(),
                task.getDescription(),
                status,
                task.getStartTime(),
                task.getDuration()
        );
    }
    @Override
    public Status getStatus() {

        return status;
    }

    @Override
    public Type getType() {

        return Type.TASK;
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
