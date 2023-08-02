package tasks;

import tasks.enums.Status;
import tasks.enums.Type;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Task extends AbstractTasks {
    private Status status;

    public Task() {
    }

    public Task(String name, String description, Status status) {
        super(name, description);
        this.status = status;
    }

    public Task(int id ,String name, String description, Status status) {
        super(name, description);
        setId(id);
        this.status = status;
    }
    public Task(String name, String description, Status status, Instant startTime, int duration) {
        super(name, description, startTime, duration);
        setEndTime(startTime.plus(duration, ChronoUnit.MINUTES));
        this.status = status;
    }

    private Task(int id, String name, String description, Status status, Instant startTime, int duration) {
        super(name, description, startTime, duration);
        setId(id);
        setEndTime(startTime.plus(duration, ChronoUnit.MINUTES));
        this.status = status;
    }
    public Task withNewStatus(Task task ,Status status) {
        Task newTask;
        if (task.getStartTime() == null&& task.getDuration() == 0) {
            newTask = withNewStatusWithoutTime(task, status);
        } else {
            newTask = withNewStatusWithTime(task, status);
        }
        return newTask;
    }

    private Task withNewStatusWithoutTime(Task task, Status status) {
        return new Task(
                task.getId(),
                task.getName(),
                task.getDescription(),
                status
        );
    }

    private Task withNewStatusWithTime(Task task, Status status) {
        return new Task(
                task.getId(),
                task.getName(),
                task.getDescription(),
                status,
                task.getStartTime(),
                task.getDuration()
        );
    }

    public void setStatus(Status status) {
        this.status = status;
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