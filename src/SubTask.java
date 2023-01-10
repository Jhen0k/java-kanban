public class SubTask extends Task {
    private final Status status;
    private Epic epic;
    private String description;

    public SubTask(int id, String name, String description, Status status, Epic epic) {
        super(id, name, description);
        this.status = status;
        this.epic = epic;
    }

    public SubTask withStatus(Status status) {
        return new SubTask(
                this.getId(),
                this.getName(),
                this.getDescription(),
                status,
                this.epic
        );
    }

    public static class ToCreateSubTaskName {
        private String subTaskName;
        private String description;

        public ToCreateSubTaskName(String subTaskName, String description) {
            this.subTaskName = subTaskName;
            this.description = description;
        }

        public String getSubTaskName() {
            return subTaskName;
        }

        public String getDescription() {
            return description;
        }
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Type getType() {
        return Type.SUB;
    }

    public Epic getEpicName() {
        return epic;
    }

    @Override
    public String toString() {
        return "SubTask{"
                + "id= " + getId()
                + ", name= " + getName() + " "
                + ", description= " + getDescription() + " "
                + ", status= " + getStatus() + " "
                + ", epicName= " + epic.getName() + " " +
                '}';
    }
}
