package tasks;
import enums.Status;
import enums.Type;

public abstract class Tasks {
    private int id;
    private final String name;
    private final String description;


    protected Tasks(String name, String description) {
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

    public void setId(int id){
        this.id = id;
    }

    public abstract Status getStatus();

    public abstract Type getType();

    public abstract String toString();
}
