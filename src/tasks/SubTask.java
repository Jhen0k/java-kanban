package tasks;
import enums.Status;
import enums.Type;

public class SubTask extends Tasks {
    private final Status status;
    private int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description);
        this.status = status;
        this.epicId = epicId;
    }

    public SubTask withStatus(Status status) {
        return new SubTask(
                this.getName(),
                this.getDescription(),
                status,
                this.epicId
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
        return getId() + "," +
                getType() + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription() + "," +
                getEpicId() + ",";
    }
}
