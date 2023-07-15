package tasks;

import enums.Status;
import enums.Type;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class Tasks {
    private int id;
    private final String name;
    private final String description;
    private Instant startTime;
    private int duration;
    private Instant getEndTime;
    protected Tasks(String name, String description) {
        this.name = name;
        this.description = description;
    }
    protected Tasks(String name, String description, Instant startTime, int duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.getEndTime = startTime.plus(duration, ChronoUnit.MINUTES);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {

        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public Instant getGetEndTime() {
        return getEndTime;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setGetEndTime(Instant getEndTime) {
        this.getEndTime = getEndTime;
    }



    public abstract Status getStatus();

    public abstract Type getType();

    public abstract String toString();
}
