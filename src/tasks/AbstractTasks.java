package tasks;

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import tasks.enums.Status;
import tasks.enums.Type;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class AbstractTasks implements EngineExecutionContext {
    private int id;
    private String name;
    private String description;
    private Instant startTime;
    private int duration;
    private Instant endTime;

    public AbstractTasks() {
    }
    protected AbstractTasks(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected AbstractTasks(String name, String description, Instant startTime, int duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration, ChronoUnit.MINUTES);
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

    public Instant getEndTime() {
        return endTime;
    }

    public final void setId(int id){
        this.id = id;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final void setDescription(String description) {
        this.description = description;
    }


    public final void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public final void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public final void setDuration(int duration) {
        this.duration = duration;
    }

    public abstract Status getStatus();

    public abstract Type getType();

    public abstract String toString();
}