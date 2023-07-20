package tasks;

import enums.Status;
import enums.Type;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class AbstractTasks implements EngineExecutionContext {
    private int id;
    private final String name;
    private final String description;
    private Instant startTime;
    private int duration;
    private Instant getEndTime;
    protected AbstractTasks(String name, String description) {
        this.name = name;
        this.description = description;
    }
    protected AbstractTasks(String name, String description, Instant startTime, int duration) {
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

    public final void setId(int id){
        this.id = id;
    }

    public final void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public final void setGetEndTime(Instant getEndTime) {
        this.getEndTime = getEndTime;
    }

    public final void setDuration(int duration) {
        this.duration = duration;
    }

    public abstract Status getStatus();

    public abstract Type getType();

    public abstract String toString();
}
