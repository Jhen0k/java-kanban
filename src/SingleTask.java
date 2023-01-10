public class SingleTask extends Task {
    private final Status status;

    public SingleTask(int id, String name, String description, Status status) {
        super(id, name, description);
        this.status = status;
    }

    public SingleTask withNewName(String name) {
        return new SingleTask(
                this.getId(),
                name,
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

        return Type.SINGLE;
    }

    @Override
    public String toString() {
        return "SingleTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
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
