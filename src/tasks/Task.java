package tasks;
import enums.Status;
import enums.Type;


public class Task extends Tasks {
    private final Status status;
    public Task(String name, String description, Status status) {
        super(name, description);
        this.status = status;
    }

    public Task withNewStatus(Status status) {
        return new Task(
                this.getName(),
                this.getDescription(),
                status
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
        return getId() + "," +
                getType() + "," +
                getName() + "," +
                getStatus() + "," +
                getDescription() + ",";
    }

    public static class ToCreateName {
        private String name;
        private String description;

        public ToCreateName(String name, String description) {

            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
