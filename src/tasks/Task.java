package tasks;
import exception.GetIdException;
import enums.Status;
import enums.Type;

public abstract class Task {
    private final int id;
    private final String name;
    private final String description;


    protected Task(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public void setId(int id) throws GetIdException {

        throw new GetIdException();
    }

    public abstract Status getStatus();

    public abstract Type getType();

    public abstract String toString();
}
