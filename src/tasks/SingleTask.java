package tasks;
import enums.Status;
import enums.Type;


public class SingleTask extends Task {
    private final Status status;
    public SingleTask(int id, String name, String description, Status status) {
        super(id, name, description);
        this.status = status;
    }

    public SingleTask withNewStatus(Status status) {
        return new SingleTask(
                this.getId(),
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
                Type.TASK + "," +
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
