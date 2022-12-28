import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    private List<SubTask> subtasks;

    public Epic(Integer id, String name) {
        super(id, name);
        this.subtasks = new ArrayList<>();
    }
    @Override
    public Status getStatus() {
        return Status.NEW;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }
}
